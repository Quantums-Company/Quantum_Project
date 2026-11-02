package org.bytebloom.logiroute.data.csv.io

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CsvLineSplitterTest {

    private lateinit var splitter: CsvLineSplitter

    @BeforeEach
    fun setUp() {
        splitter = CsvLineSplitter()
    }

    @Test
    fun `should split a plain line on commas`() {
        // Given
        val line = "WH-1,Ramallah Hub,WEST"

        // When
        val columns = splitter.split(line)

        // Then
        assertThat(columns).containsExactly("WH-1", "Ramallah Hub", "WEST").inOrder()
    }

    @Test
    fun `should keep a comma inside a quoted value when splitting`() {
        // Given
        val line = "WH-1,\"Hub, North\",WEST"

        // When
        val columns = splitter.split(line)

        // Then
        assertThat(columns).containsExactly("WH-1", "Hub, North", "WEST").inOrder()
    }

    @Test
    fun `should read a doubled quote as one quote character when splitting`() {
        // Given
        val line = "WH-1,\"The \"\"Big\"\" Hub\",WEST"

        // When
        val columns = splitter.split(line)

        // Then
        assertThat(columns).containsExactly("WH-1", "The \"Big\" Hub", "WEST").inOrder()
    }

    @Test
    fun `should keep empty columns when a line has consecutive commas`() {
        // Given
        val line = "WH-1,,WEST"

        // When
        val columns = splitter.split(line)

        // Then
        assertThat(columns).containsExactly("WH-1", "", "WEST").inOrder()
    }
}
