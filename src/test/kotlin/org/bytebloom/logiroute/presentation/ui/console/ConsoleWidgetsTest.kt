package org.bytebloom.logiroute.presentation.ui.console

import org.bytebloom.logiroute.presentation.ui.console.widget.box
import org.bytebloom.logiroute.presentation.ui.console.widget.formatNumber
import org.bytebloom.logiroute.presentation.ui.console.widget.padVisible
import org.bytebloom.logiroute.presentation.ui.console.widget.percentBar
import org.bytebloom.logiroute.presentation.ui.console.widget.table
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConsoleWidgetsTest {

    private val plain = ConsoleStyle(colors = false, unicode = true)
    private val colored = ConsoleStyle(colors = true, unicode = true)
    private val ascii = ConsoleStyle(colors = false, unicode = false)

    private fun widths(lines: List<String>) = lines.map { ConsoleStyle.visibleLength(it) }.toSet()

    @Test
    fun `colour codes take no room on screen`() {
        // Given / When
        val length = ConsoleStyle.visibleLength(colored.red("hello"))

        // Then
        assertEquals(5, length)
    }

    @Test
    fun `colours are switched off when disabled`() {
        // Given / When
        val text = plain.red("hello")

        // Then
        assertEquals("hello", text)
    }

    @Test
    fun `padding ignores colour codes`() {
        // Given / When
        val padded = padVisible(colored.green("ab"), 5)

        // Then
        assertEquals(5, ConsoleStyle.visibleLength(padded))
    }

    @Test
    fun `padding can align to the right`() {
        // Given / When
        val padded = padVisible("7", 3, alignRight = true)

        // Then
        assertEquals("  7", padded)
    }

    @Test
    fun `a box has the same width on every line`() {
        // Given / When
        val lines = plain.box("Route", listOf("Ramallah → Nablus", "65 km"))

        // Then
        assertEquals(1, widths(lines).size)
    }

    @Test
    fun `a box with a coloured title still lines up`() {
        // Given / When
        val lines = colored.box(colored.red("Problem"), listOf("short"))

        // Then
        assertEquals(1, widths(lines).size)
    }

    @Test
    fun `a box grows to fit a long title`() {
        // Given / When
        val lines = plain.box("A rather long title", listOf("x"))

        // Then
        assertEquals(1, widths(lines).size)
    }

    @Test
    fun `a table has the same width on every line`() {
        // Given / When
        val lines = plain.table(listOf("ID", "Name"), listOf(listOf("WH-001", "Ramallah"), listOf("WH-2", "Al-Bireh")))

        // Then
        assertEquals(1, widths(lines).size)
    }

    @Test
    fun `a table with coloured cells still lines up`() {
        // Given / When
        val lines = colored.table(listOf("Priority"), listOf(listOf(colored.red("URGENT")), listOf(colored.green("LOW"))))

        // Then
        assertEquals(1, widths(lines).size)
    }

    @Test
    fun `a table lists every row between its borders`() {
        // Given / When
        val lines = plain.table(listOf("ID"), listOf(listOf("A"), listOf("B"), listOf("C")))

        // Then
        assertEquals(3 + 4, lines.size)
    }

    @Test
    fun `numbers can be aligned to the right`() {
        // Given / When
        val lines = plain.table(listOf("N"), listOf(listOf("5"), listOf("1234")), rightAligned = setOf(0))

        // Then
        assertTrue(lines.any { it.contains("│    5 │") })
    }

    @Test
    fun `ascii mode never draws a non ascii character`() {
        // Given / When
        val lines = ascii.box("Title", listOf("body")) + ascii.table(listOf("A"), listOf(listOf("b"))) + ascii.percentBar(50.0)

        // Then
        assertTrue(lines.all { line -> line.all { it.code < 128 } })
    }

    @Test
    fun `a full bar is completely filled`() {
        // Given / When
        val bar = plain.percentBar(100.0, width = 10)

        // Then
        assertTrue(bar.startsWith("██████████"))
    }

    @Test
    fun `an empty bar is completely hollow`() {
        // Given / When
        val bar = plain.percentBar(0.0, width = 10)

        // Then
        assertTrue(bar.startsWith("░░░░░░░░░░"))
    }

    @Test
    fun `a bar never overflows for impossible percentages`() {
        // Given / When
        val bar = plain.percentBar(250.0, width = 10)

        // Then
        assertEquals(10, bar.count { it == '█' })
    }

    @Test
    fun `numbers always use a dot as decimal separator`() {
        // Given / When
        val text = formatNumber(1234.5678, 2)

        // Then
        assertEquals("1234.57", text)
    }

    @Test
    fun `menu accepts the option number`() {
        // Given / When
        val option = MenuOption.parse("2")

        // Then
        assertEquals(MenuOption.ROUTE, option)
    }

    @Test
    fun `menu accepts a word in any case with spaces around`() {
        // Given / When
        val option = MenuOption.parse("  KNN ")

        // Then
        assertEquals(MenuOption.CLASSIFY, option)
    }

    @Test
    fun `menu accepts quit as an alias of exit`() {
        // Given / When
        val option = MenuOption.parse("quit")

        // Then
        assertEquals(MenuOption.EXIT, option)
    }

    @Test
    fun `menu rejects unknown input`() {
        // Given / When
        val option = MenuOption.parse("banana")

        // Then
        assertNull(option)
    }
}
