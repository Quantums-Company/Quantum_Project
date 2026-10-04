package architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

class CleanArchitectureTest {
    private val scope by lazy { Konsist.scopeFromProject() }
    private val presentationLayer by lazy { Layer("presentation", "org.bytebloom.presentation..") }
    private val domainLayer by lazy { Layer("domain", "org.bytebloom.domain..") }
    private val dataLayer by lazy { Layer("data", "org.bytebloom.data..") }
    private val diLayer by lazy { Layer("di", "org.bytebloom.di..") }

    @Test
    fun `clean architecture layers have correct dependencies`() {
        scope.assertArchitecture {
            presentationLayer.dependsOn(domainLayer, diLayer)
            diLayer.dependsOn(domainLayer, dataLayer)
            dataLayer.dependsOn(domainLayer)
            domainLayer.dependsOnNothing()
        }
    }

    @Test
    fun `domain layer must not import third party libraries`() {
        scope
            .files
            .withPackage("org.bytebloom.domain..")
            .assertTrue { file ->
                file.imports.none { import ->
                    import.name.contains("supabase") ||
                            import.name.contains("postgrest") ||
                            import.name.contains("sql")
                }
            }
    }

    @Test
    fun `use case classes must end with UseCase suffix`() {
        scope
            .classes()
            .withPackage("org.bytebloom.domain.usecase..")
            .assertTrue { it.hasNameEndingWith("UseCase") }
    }

    @Test
    fun `validator classes must end with Validator suffix`() {
        scope
            .classes()
            .withPackage("org.bytebloom.domain.validator..")
            .assertTrue { it.hasNameEndingWith("Validator") }
    }

    @Test
    fun `invoke functions inside use cases must have operator modifier`() {
        scope
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue {
                it.hasFunction { function ->
                    function.name == "invoke" &&
                            function.hasPublicOrDefaultModifier &&
                            function.hasOperatorModifier
                }
            }
    }

    @Test
    fun `dto classes must end with Dto suffix`() {
        scope
            .classes()
            .withPackage("org.bytebloom.data.remote.dto..")
            .assertTrue { it.hasNameEndingWith("Dto") }
    }

    @Test
    fun `dto classes must be annotated with Serializable`() {
        scope
            .classes()
            .withPackage("org.bytebloom.data.remote.dto..")
            .assertTrue { it.hasAnnotationOf(Serializable::class) }
    }
}