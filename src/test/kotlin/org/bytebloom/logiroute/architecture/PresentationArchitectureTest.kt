package org.bytebloom.logiroute.architecture

import com.google.common.truth.Truth.assertThat
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.ext.list.withPackage
import org.junit.jupiter.api.Test

/**
 * Keeps the UI replaceable. The presenter talks to a screen INTERFACE only, so a terminal UI
 * can be swapped for an Android UI by writing new classes that implement the same interfaces.
 *
 * File naming convention used by every feature:
 *  - presentation/screens/XScreen.kt       the contract (interface) between presenter and UI
 *  - presentation/presenter/XPresenter.kt  the logic: uses use-cases, drives the screen
 *  - ui/screens/XTerminalUI.kt             one implementation of the screen (the only terminal-specific part)
 */
class PresentationArchitectureTest {
    private val presentationFiles by lazy {
        Konsist.scopeFromProduction().files.withPackage("org.bytebloom.logiroute.presentation..")
    }

    private val uiFiles by lazy {
        Konsist.scopeFromProduction().files.withPackage("org.bytebloom.logiroute.ui..")
    }

    private val uiPackage = "org.bytebloom.logiroute.ui"
    private val platformApis = arrayOf("java.io", "java.awt", "javax.swing", "android")
    private val outerLayers = arrayOf("org.bytebloom.logiroute.data", "org.bytebloom.logiroute.di", "org.koin")

    private fun files(suffix: String): List<KoFileDeclaration> =
        presentationFiles.filter { it.name.endsWith(suffix) }

    private fun terminalUiFiles(): List<KoFileDeclaration> =
        uiFiles.filter { it.name.endsWith("TerminalUI") }

    private fun offenders(files: List<KoFileDeclaration>, vararg forbiddenPrefixes: String): List<String> =
        files
            .filter { file -> file.imports.any { import -> forbiddenPrefixes.any { import.name.startsWith(it) } } }
            .map { it.name }

    @Test
    fun `should find presenters screens and terminal uis so the rules below are not vacuous`() {
        // Given / When
        val presenters = files("Presenter")
        val screens = files("Screen")
        val terminalUis = terminalUiFiles()

        // Then
        assertThat(presenters).isNotEmpty()
        assertThat(screens).isNotEmpty()
        assertThat(terminalUis).isNotEmpty()
    }

    @Test
    fun `should keep presenters unaware of any ui`() {
        // Given / When
        val offenders = offenders(files("Presenter"), uiPackage)
        val importingAnImplementation = files("Presenter")
            .filter { file -> file.imports.any { it.name.endsWith("TerminalUI") } }
            .map { it.name }

        // Then
        assertThat(offenders).isEmpty()
        assertThat(importingAnImplementation).isEmpty()
    }

    @Test
    fun `should keep presenters free from console and desktop apis`() {
        // Given / When
        val offenders = offenders(files("Presenter"), *platformApis)

        // Then
        assertThat(offenders).isEmpty()
    }

    @Test
    fun `should keep presenters free from data di and koin classes`() {
        // Given / When
        val offenders = offenders(files("Presenter"), *outerLayers)

        // Then
        assertThat(offenders).isEmpty()
    }

    @Test
    fun `should keep screen contracts free from any ui implementation`() {
        // Given / When
        val offenders = offenders(files("Screen"), uiPackage, *platformApis, *outerLayers)

        // Then
        assertThat(offenders).isEmpty()
    }

    @Test
    fun `should let terminal uis only display and not call use cases or repositories`() {
        // Given / When
        val offenders = offenders(
            terminalUiFiles(),
            "org.bytebloom.logiroute.domain.usecase",
            "org.bytebloom.logiroute.domain.repository",
            *outerLayers
        )

        // Then
        assertThat(offenders).isEmpty()
    }
}
