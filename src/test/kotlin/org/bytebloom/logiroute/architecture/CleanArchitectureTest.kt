package org.bytebloom.logiroute.architecture

import com.google.common.truth.Truth.assertThat
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

class CleanArchitectureTest {
    private val scope by lazy { Konsist.scopeFromProduction() }

    private val presentationLayer by lazy { Layer("presentation", "org.bytebloom.logiroute.presentation..") }
    private val domainLayer by lazy { Layer("domain", "org.bytebloom.logiroute.domain..") }
    private val dataLayer by lazy { Layer("data", "org.bytebloom.logiroute.data..") }
    private val diLayer by lazy { Layer("di", "org.bytebloom.logiroute.di..") }

    @Test
    fun `should keep correct dependencies between clean architecture layers`() {
        // Given
        val productionScope = scope

        // When
        val violation = runCatching {
            productionScope.assertArchitecture {
                presentationLayer.dependsOn(domainLayer, diLayer)
                diLayer.dependsOn(domainLayer, dataLayer)
                dataLayer.dependsOn(domainLayer)
                domainLayer.dependsOnNothing()
            }
        }.exceptionOrNull()

        // Then
        assertThat(violation).isNull()
    }

    @Test
    fun `should not import third party libraries in domain layer`() {
        // Given
        val domainFiles = scope.files.withPackage("org.bytebloom.logiroute.domain..")

        // When
        val filesWithThirdPartyImports = domainFiles
            .filter { file ->
                file.imports.any { import ->
                    import.name.contains("supabase") ||
                            import.name.contains("postgrest") ||
                            import.name.contains("sql")
                }
            }
            .map { it.name }

        // Then
        assertThat(filesWithThirdPartyImports).isEmpty()
    }

    @Test
    fun `should end use case class names with UseCase suffix`() {
        // Given
        val useCaseClasses = scope.classes().withPackage("org.bytebloom.logiroute.domain.usecase..")

        // When
        val invalidClassNames = useCaseClasses
            .filterNot { it.hasNameEndingWith("UseCase") }
            .map { it.name }

        // Then
        assertThat(invalidClassNames).isEmpty()
    }

    @Test
    fun `should end validator class names with Validator suffix`() {
        // Given
        val validatorClasses = scope.classes().withPackage("org.bytebloom.logiroute.domain.validator..")

        // When
        val invalidClassNames = validatorClasses
            .filterNot { it.hasNameEndingWith("Validator") }
            .map { it.name }

        // Then
        assertThat(invalidClassNames).isEmpty()
    }

    @Test
    fun `should mark invoke function as public operator in every use case`() {
        // Given
        val useCaseClasses = scope.classes().withNameEndingWith("UseCase")

        // When
        val invalidClassNames = useCaseClasses
            .filterNot {
                it.hasFunction { function ->
                    function.name == "invoke" &&
                            function.hasPublicOrDefaultModifier &&
                            function.hasOperatorModifier
                }
            }
            .map { it.name }

        // Then
        assertThat(invalidClassNames).isEmpty()
    }

    @Test
    fun `should end dto class names with Dto suffix`() {
        // Given
        val dtoClasses = scope.classes().withPackage("org.bytebloom.logiroute.data.remote.dto..")

        // When
        val invalidClassNames = dtoClasses
            .filterNot { it.hasNameEndingWith("Dto") }
            .map { it.name }

        // Then
        assertThat(invalidClassNames).isEmpty()
    }

    @Test
    fun `should annotate every dto class with Serializable`() {
        // Given
        val dtoClasses = scope.classes().withPackage("org.bytebloom.logiroute.data.remote.dto..")

        // When
        val invalidClassNames = dtoClasses
            .filterNot { it.hasAnnotationOf(Serializable::class) }
            .map { it.name }

        // Then
        assertThat(invalidClassNames).isEmpty()
    }
}
