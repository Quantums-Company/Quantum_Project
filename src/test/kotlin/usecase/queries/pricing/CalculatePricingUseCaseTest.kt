package usecase.queries.pricing

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.NetworkUnavailableException
import org.bytebloom.domain.model.exception.UnknownDataException
import org.bytebloom.domain.pricing.core.PricingOptions
import org.bytebloom.domain.pricing.decorator.ColdChainDecorator
import org.bytebloom.domain.pricing.decorator.DecoratorFee
import org.bytebloom.domain.pricing.decorator.DecoratorType
import org.bytebloom.domain.pricing.decorator.FragileHandlingDecorator
import org.bytebloom.domain.pricing.factory.DecoratorFactory
import org.bytebloom.domain.pricing.factory.StrategyFactory
import org.bytebloom.domain.pricing.strategy.DispatchStrategy
import org.bytebloom.domain.pricing.strategy.ShippingStrategyType
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.usecase.queries.pricing.CalculatePricingUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class CalculatePricingUseCaseTest {

    private lateinit var strategyFactory: StrategyFactory
    private lateinit var decoratorFactory: DecoratorFactory
    private lateinit var routeRepository: RouteRepository
    private lateinit var calculatePricing: CalculatePricingUseCase

    @BeforeEach
    fun setUp() {
        strategyFactory = mockk()
        decoratorFactory = mockk()
        routeRepository = mockk()

        val strategy = mockk<DispatchStrategy>()
        every { strategy.calculateTransitCost(any(), any()) } returns BASE_COST
        every { strategy.getPriorityMultiplier(any()) } returns PRIORITY_MULTIPLIER
        every { strategyFactory.getStrategy(ShippingStrategyType.ECO) } returns strategy
        coEvery { routeRepository.getAll() } returns listOf(MATCHING_ROUTE)

        calculatePricing = CalculatePricingUseCase(strategyFactory, decoratorFactory, routeRepository)
    }

    @Test
    fun `should return base cost multiplied by priority multiplier when no decorators are requested`() = runTest {
        // Given
        val options = PricingOptions(pkg = PACKAGE, strategy = ShippingStrategyType.ECO)

        // When
        val price = calculatePricing(options)

        // Then
        assertThat(price).isEqualTo(BASE_COST * PRIORITY_MULTIPLIER)
    }

    @Test
    fun `should add decorator fee on top of base price`() = runTest {
        // Given
        val fragileFee = DecoratorFee(DecoratorType.FRAGILE_HANDLING, 20.0)
        every { decoratorFactory.createDecorator(any(), fragileFee) } answers {
            FragileHandlingDecorator(firstArg(), fragileFee.value)
        }
        val options = PricingOptions(PACKAGE, ShippingStrategyType.ECO, decorators = listOf(fragileFee))

        // When
        val price = calculatePricing(options)

        // Then
        assertThat(price).isEqualTo(BASE_COST * PRIORITY_MULTIPLIER + 20.0)
    }

    @Test
    fun `should apply decorators in the order they are requested`() = runTest {
        // Given
        val fragileFee = DecoratorFee(DecoratorType.FRAGILE_HANDLING, 20.0)
        val coldChainFee = DecoratorFee(DecoratorType.COLD_CHAIN, 2.0)
        every { decoratorFactory.createDecorator(any(), fragileFee) } answers {
            FragileHandlingDecorator(firstArg(), fragileFee.value)
        }
        every { decoratorFactory.createDecorator(any(), coldChainFee) } answers {
            ColdChainDecorator(firstArg(), coldChainFee.value)
        }
        val options = PricingOptions(PACKAGE, ShippingStrategyType.ECO, decorators = listOf(fragileFee, coldChainFee))

        // When
        val price = calculatePricing(options)

        // Then
        assertThat(price).isEqualTo((BASE_COST * PRIORITY_MULTIPLIER + 20.0) * 2.0)
    }

    @Test
    fun `should ignore decorator that factory cannot create`() = runTest {
        // Given
        val unsupportedFee = DecoratorFee(DecoratorType.EXPRESS_INSURANCE, 15.0)
        every { decoratorFactory.createDecorator(any(), unsupportedFee) } returns null
        val options = PricingOptions(PACKAGE, ShippingStrategyType.ECO, decorators = listOf(unsupportedFee))

        // When
        val price = calculatePricing(options)

        // Then
        assertThat(price).isEqualTo(BASE_COST * PRIORITY_MULTIPLIER)
    }

    @Test
    fun `should throw validation exception and skip route lookup when strategy is unknown`() = runTest {
        // Given
        every { strategyFactory.getStrategy(ShippingStrategyType.EXPRESS) } returns null
        val options = PricingOptions(PACKAGE, ShippingStrategyType.EXPRESS)

        // When & Then
        assertFailsWith<EntityValidationException> { calculatePricing(options) }
        coVerify(exactly = 0) { routeRepository.getAll() }
    }

    @Test
    fun `should throw unknown data exception when no route matches the package`() = runTest {
        // Given
        coEvery { routeRepository.getAll() } returns emptyList()
        val options = PricingOptions(PACKAGE, ShippingStrategyType.ECO)

        // When & Then
        assertFailsWith<UnknownDataException> { calculatePricing(options) }
    }

    @Test
    fun `should propagate network exception when route repository is unreachable`() = runTest {
        // Given
        coEvery { routeRepository.getAll() } throws NetworkUnavailableException()
        val options = PricingOptions(PACKAGE, ShippingStrategyType.ECO)

        // When & Then
        assertFailsWith<NetworkUnavailableException> { calculatePricing(options) }
    }

    private companion object {
        const val BASE_COST = 100.0
        const val PRIORITY_MULTIPLIER = 1.5
        val ORIGIN = createWarehouse(id = "WH-001", name = "Ramallah Hub")
        val DESTINATION = createWarehouse(id = "WH-002", name = "Nablus Depot")

        val PACKAGE = Package(
            id = "PKG-001",
            weight = 10.0,
            priority = Priority.STANDARD,
            originWarehouse = ORIGIN,
            destinationWarehouse = DESTINATION
        )

        val MATCHING_ROUTE = Route(
            id = "RT-001",
            distanceKm = 120.0,
            typicalDelayMin = 15,
            originWarehouse = ORIGIN,
            destinationWarehouse = DESTINATION
        )

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "West Bank",
            longitude = 35.2,
            latitude = 31.9
        )
    }
}
