package org.bytebloom.logiroute.architecture

import com.google.common.truth.Truth.assertThat
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import org.junit.jupiter.api.Test

/**
 * Keeps the UI replaceable: the console UI can be swapped for Compose/Android
 * only if it depends on presenters and UI models and on nothing else.
 */
class PresentationArchitectureTest {
    private val scope by lazy { Konsist.scopeFromProduction() }

    private val uiPackage = "org.bytebloom.logiroute.presentation.ui.."
    private val presenterPackage = "org.bytebloom.logiroute.presentation.presenter.."
    private val modelPackage = "org.bytebloom.logiroute.presentation.model.."

    private fun filesImporting(packageName: String, vararg forbiddenPrefixes: String): List<String> =
        scope.files
            .withPackage(packageName)
            .filter { file -> file.imports.any { import -> forbiddenPrefixes.any { import.name.startsWith(it) } } }
            .map { it.name }

    @Test
    fun `should keep the ui free from domain data and di classes`() {
        // Given / When
        val offenders = filesImporting(
            uiPackage,
            "org.bytebloom.logiroute.domain",
            "org.bytebloom.logiroute.data",
            "org.bytebloom.logiroute.di",
            "org.koin"
        )

        // Then
        assertThat(offenders).isEmpty()
    }

    @Test
    fun `should keep presenters unaware of any ui`() {
        // Given / When
        val offenders = filesImporting(presenterPackage, "org.bytebloom.logiroute.presentation.ui")

        // Then
        assertThat(offenders).isEmpty()
    }

    @Test
    fun `should keep presenters free from console and desktop apis`() {
        // Given / When
        val offenders = filesImporting(presenterPackage, "java.io", "java.awt", "javax.swing", "android")

        // Then
        assertThat(offenders).isEmpty()
    }

    @Test
    fun `should keep ui models plain without domain data or presenter dependencies`() {
        // Given / When
        val offenders = filesImporting(
            modelPackage,
            "org.bytebloom.logiroute.domain",
            "org.bytebloom.logiroute.data",
            "org.bytebloom.logiroute.presentation.presenter",
            "org.bytebloom.logiroute.presentation.ui"
        )

        // Then
        assertThat(offenders).isEmpty()
    }
}
