package org.bytebloom.logiroute.data.csv.io

import java.nio.file.Path
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CsvFileWriterTest {

    @TempDir
    lateinit var tempDirectory: Path

    @Test
    fun `writes and replaces a complete csv snapshot`() = runTest {
        val file = tempDirectory.resolve("snapshot.csv").toFile()
        val writer = CsvFileWriter()

        writer.write(file, "id,name", listOf("1,first", "2,second"))
        assertEquals(
            listOf("id,name", "1,first", "2,second"),
            file.readLines()
        )

        writer.write(file, "id,name", listOf("3,updated"))

        assertEquals(listOf("id,name", "3,updated"), file.readLines())
        assertTrue(tempDirectory.toFile().listFiles().orEmpty().none { it.extension == "tmp" })
    }
}
