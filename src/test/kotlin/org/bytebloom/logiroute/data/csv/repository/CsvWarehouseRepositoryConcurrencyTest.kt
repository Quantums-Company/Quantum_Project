package org.bytebloom.logiroute.data.csv.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.csv.datasource.WarehouseDataSource
import org.bytebloom.logiroute.data.csv.raw.WarehouseRaw
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class CsvWarehouseRepositoryConcurrencyTest {

    @Test
    fun `concurrent first reads load the CSV data only once`() = runTest {
        val loadCount = AtomicInteger()
        val dataSource = object : WarehouseDataSource {
            override suspend fun loadAll(): List<WarehouseRaw> {
                loadCount.incrementAndGet()
                delay(10.milliseconds)
                return listOf(
                    WarehouseRaw(
                        id = "WH-001",
                        name = "Central Hub",
                        regionalZone = "West Bank",
                        longitude = 35.2,
                        latitude = 31.9
                    )
                )
            }
        }
        val repository = CsvWarehouseRepository(dataSource)

        val results = coroutineScope {
            List(20) { async { repository.getAll() } }.awaitAll()
        }

        assertEquals(1, loadCount.get())
        assertEquals(20, results.size)
        assertEquals("WH-001", results.first().single().id)
    }

    @Test
    fun `mutating returned list does not mutate cached list`() = runTest {
        val repository = CsvWarehouseRepository(object : WarehouseDataSource {
            override suspend fun loadAll(): List<WarehouseRaw> = listOf(
                WarehouseRaw(
                    id = "WH-001",
                    name = "Central Hub",
                    regionalZone = "West Bank",
                    longitude = 35.2,
                    latitude = 31.9
                )
            )
        })

        val exposed = repository.getAll()
        runCatching { (exposed as MutableList).clear() }

        assertEquals(1, repository.getAll().size)
    }
}
