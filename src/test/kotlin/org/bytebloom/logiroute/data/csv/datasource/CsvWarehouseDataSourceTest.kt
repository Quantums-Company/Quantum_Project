package org.bytebloom.logiroute.data.csv.datasource

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.csv.io.CsvFileReader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class CsvWarehouseDataSourceTest {

    @TempDir
    lateinit var tempDirectory: Path

    @Test
    fun `should load a warehouse whose name contains a comma`() = runTest {
        // Given
        tempDirectory.resolve("warehouses.csv").toFile().writeText(
            "id,name,regionalZone,latitude,longitude\n" +
                "WH-1,\"Hub, North\",NORTH,32.2,35.3\n"
        )
        val dataSource = CsvWarehouseDataSource(
            CsvFileReader(
                cacheDirectory = tempDirectory.toString(),
                seedDirectory = tempDirectory.toString()
            )
        )

        // When
        val warehouses = dataSource.loadAll()

        // Then
        assertThat(warehouses.single().name).isEqualTo("Hub, North")
    }
}
