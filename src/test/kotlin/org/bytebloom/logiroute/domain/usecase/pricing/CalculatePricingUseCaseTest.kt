package org.bytebloom.logiroute.domain.usecase.pricing

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.model.exception.UnknownDataException
import org.bytebloom.logiroute.domain.pricing.core.PricingOptions
import org.bytebloom.logiroute.domain.pricing.decorator.ColdChainDecorator
import org.bytebloom.logiroute.domain.pricing.decorator.DecoratorFee
import org.bytebloom.logiroute.domain.pricing.decorator.DecoratorType
import org.bytebloom.logiroute.domain.pricing.decorator.FragileHandlingDecorator
import org.bytebloom.logiroute.domain.pricing.factory.DecoratorFactory
import org.bytebloom.logiroute.domain.pricing.factory.StrategyFactory
import org.bytebloom.logiroute.domain.pricing.strategy.DispatchStrategy
import org.bytebloom.logiroute.domain.pricing.strategy.ShippingStrategyType
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class CalculatePricingUseCaseTest {

    private val strategyFactory: StrategyFactory = mockk()
    private val decoratorFactory: DecoratorFactory = mockk()
    private val routeRepository: RouteRepository = mockk()
    private val strategy: DispatchStrategy = mockk()
    private lateinit var useCase: CalculatePricingUseCase

    private val baseCost = 100.0
    private val priorityMultiplier = 1.5
    private val basePrice = baseCost * priorityMultiplier
    private val originWarehouse = createWarehouse(id = "WH-001", name = "Ramallah Hub")
    private val destinationWarehouse = createWarehouse(id = "WH-002", name = "Nablus Depot")
    private val packageToShip = createPackage(
        originWarehouse = originWarehouse,
        destinationWarehouse = destinationWarehouse
    )
    private val matchingRoute = createRoute(
        originWarehouse = originWarehouse,
        destinationWarehouse = destinationWarehouse
    )

    @BeforeEach
    fun setUp() {
        every { strategy.calculateTransitCost(any(), any()) } returns baseCost
        every { strategy.getPriorityMultiplier(any()) } returns priorityMultiplier
        every { strategyFactory.getStrategy(ShippingStrategyType.ECO) } returns strategy
        coEvery { routeRepository.getAll() } returns listOf(matchingRoute)

        useCase = CalculatePricingUseCase(strategyFactory, decoratorFactory, routeRepository)
    }

    @Test
    fun `should return base cost multiplied by priority multiplier when no decorators are requested`() = runTest {
        // Given
        val options = PricingOptions(pkg = packageToShip, strategy = ShippingStrategyType.ECO)

        // When
        val price = useCase(options)

        // Then
        assertThat(price).isEqualTo(basePrice)
    }

    @Test
    fun `should add decorator fee on top of base price when a decorator is requested`() = runTest {
        // Given
        val fragileFeeValue = 20.0
        val fragileFee = DecoratorFee(DecoratorType.FRAGILE_HANDLING, fragileFeeValue)
        every { decoratorFactory.createDecorator(any(), fragileFee) } answers {
            FragileHandlingDecorator(firstArg(), fragileFee.value)
        }
        val options = PricingOptions(packageToShip, ShippingStrategyType.ECO, decorators = listOf(fragileFee))

        // When
        val price = useCase(options)

        // Then
        assertThat(price).isEqualTo(basePrice + fragileFeeValue)
    }

    @Test
    fun `should apply decorators in the order they are requested`() = runTest {
        // Given
        val fragileFeeValue = 20.0
        val coldChainMultiplier = 2.0
        val fragileFee = DecoratorFee(DecoratorType.FRAGILE_HANDLING, fragileFeeValue)
        val coldChainFee = DecoratorFee(DecoratorType.COLD_CHAIN, coldChainMultiplier)
        every { decoratorFactory.createDecorator(any(), fragileFee) } answers {
            FragileHandlingDecorator(firstArg(), fragileFee.value)
        }
        every { decoratorFactory.createDecorator(any(), coldChainFee) } answers {
            ColdChainDecorator(firstArg(), coldChainFee.value)
        }
        val options = PricingOptions(
            packageToShip,
            ShippingStrategyType.ECO,
            decorators = listOf(fragileFee, coldChainFee)
        )

        // When
        val price = useCase(options)

        // Then
        assertThat(price).isEqualTo((basePrice + fragileFeeValue) * coldChainMultiplier)
    }

    @Test
    fun `should ignore decorator when factory cannot create it`() = runTest {
        // Given
        val unsupportedFee = DecoratorFee(DecoratorType.EXPRESS_INSURANCE, 15.0)
        every { decoratorFactory.createDecorator(any(), unsupportedFee) } returns null
        val options = PricingOptions(packageToShip, ShippingStrategyType.ECO, decorators = listOf(unsupportedFee))

        // When
        val price = useCase(options)

        // Then
        assertThat(price).isEqualTo(basePrice)
    }

    @Test
    fun `should throw validation exception when strategy is unknown`() = runTest {
        // Given
        every { strategyFactory.getStrategy(ShippingStrategyType.EXPRESS) } returns null
        val options = PricingOptions(packageToShip, ShippingStrategyType.EXPRESS)

        // When
        val action = suspend { useCase(options) }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should not look up routes when strategy is unknown`() = runTest {
        // Given
        every { strategyFactory.getStrategy(ShippingStrategyType.EXPRESS) } returns null
        val options = PricingOptions(packageToShip, ShippingStrategyType.EXPRESS)

        // When
        runCatching { useCase(options) }

        // Then
        coVerify(exactly = 0) { routeRepository.getAll() }
    }

    @Test
    fun `should throw unknown data exception when no route matches the package`() = runTest {
        // Given
        coEvery { routeRepository.getAll() } returns emptyList()
        val options = PricingOptions(packageToShip, ShippingStrategyType.ECO)

        // When
        val action = suspend { useCase(options) }

        // Then
        assertFailsWith<UnknownDataException> { action() }
    }

    @Test
    fun `should propagate network exception when route repository is unreachable`() = runTest {
        // Given
        coEvery { routeRepository.getAll() } throws NetworkUnavailableException()
        val options = PricingOptions(packageToShip, ShippingStrategyType.ECO)

        // When
        val action = suspend { useCase(options) }

        // Then
        assertFailsWith<NetworkUnavailableException> { action() }
    }

    private fun createWarehouse(
        id: String = "WH-001",
        name: String = "Ramallah Hub",
        regionalZone: String = "West Bank",
        longitude: Double = 35.2,
        latitude: Double = 31.9
    ) = Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )

    private fun createPackage(
        id: String = "PKG-001",
        weight: Double = 10.0,
        priority: Priority = Priority.STANDARD,
        originWarehouse: Warehouse = createWarehouse(id = "WH-001"),
        destinationWarehouse: Warehouse = createWarehouse(id = "WH-002", name = "Nablus Depot")
    ) = Package(
        id = id,
        weight = weight,
        priority = priority,
        originWarehouse = originWarehouse,
        destinationWarehouse = destinationWarehouse
    )

    private fun createRoute(
        id: String = "RT-001",
        distanceKm: Double = 120.0,
        typicalDelayMin: Int = 15,
        originWarehouse: Warehouse = createWarehouse(id = "WH-001"),
        destinationWarehouse: Warehouse = createWarehouse(id = "WH-002", name = "Nablus Depot")
    ) = Route(
        id = id,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin,
        originWarehouse = originWarehouse,
        destinationWarehouse = destinationWarehouse
    )
}
