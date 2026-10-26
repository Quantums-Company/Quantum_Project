package org.bytebloom.logiroute.data.csv.io

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

class CsvFileWriter(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        private const val TEMP_PREFIX_MIN_LENGTH = 3
    }

    suspend fun write(
        file: File,
        header: String,
        rows: List<String>
    ): Unit = withContext(ioDispatcher) {
        val destination: Path = file.absoluteFile.toPath()
        val parent: Path = requireNotNull(destination.parent)
        Files.createDirectories(parent)

        val temporaryPrefix = "${file.name}.".padEnd(TEMP_PREFIX_MIN_LENGTH, '_')
        val temporary: Path = Files.createTempFile(parent, temporaryPrefix, ".tmp")
        try {
            Files.newBufferedWriter(temporary).use { writer ->
                writer.write(header)
                writer.newLine()

                rows.forEach { row ->
                    writer.write(row)
                    writer.newLine()
                }
            }

            try {
                Files.move(temporary, destination, ATOMIC_MOVE, REPLACE_EXISTING)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temporary, destination, REPLACE_EXISTING)
            } catch (_: FileAlreadyExistsException) {
                // Some file-system providers do not replace an existing target during ATOMIC_MOVE.
                Files.move(temporary, destination, REPLACE_EXISTING)
            }
        } finally {
            Files.deleteIfExists(temporary)
        }
    }
}