package org.bytebloom.logiroute.data.csv.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bytebloom.logiroute.data.csv.io.CsvFileWriter
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.data.csv.io.CsvTable
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class CsvSnapshotStore(
    private val writer: CsvFileWriter,
    private val cacheDirectory: File = File("src/resources/cache")
) {
    private val snapshotLocks = ConcurrentHashMap<String, Mutex>()

    companion object {
        private val SPECIAL_CSV_CHARS = charArrayOf(',', '"', '\n', '\r')
    }

    private fun String.containsSpecialCsvChars(): Boolean =
        any { it in SPECIAL_CSV_CHARS }

    suspend fun saveWarehouses(items: List<Warehouse>) {
        save(
            CsvTable.WAREHOUSES.fileName,
            "id,name,regionalZone,latitude,longitude",
            items.map {
                toCsvRow(
                    it.id, it.name, it.regionalZone,
                    it.latitude.toString(), it.longitude.toString()
                )
            }
        )
    }

    suspend fun saveRoutes(items: List<Route>) {
        save(
            CsvTable.ROUTES.fileName,
            "routeId,originHubId,destinationHubId,distanceKm,typicalDelayMin",
            items.map {
                toCsvRow(
                    it.id, it.originWarehouse.id, it.destinationWarehouse.id,
                    it.distanceKm.toString(), it.typicalDelayMin.toString()
                )
            }
        )
    }

    suspend fun saveVehicles(items: List<Vehicle>) {
        save(
            CsvTable.VEHICLES.fileName,
            "vehicleId,currentHubId,maxCapacityKg,costPerKm",
            items.map {
                toCsvRow(
                    it.id, it.currentWarehouse.id,
                    it.maxCapacityKg.toString(), it.costPerKm.toString()
                )
            }
        )
    }

    suspend fun savePackages(items: List<Package>) {
        save(
            CsvTable.PACKAGES.fileName,
            "id,weight,originHubId,destinationHubId,priority",
            items.map {
                toCsvRow(
                    it.id, it.weight.toString(),
                    it.originWarehouse.id, it.destinationWarehouse.id,
                    it.priority.name
                )
            }
        )
    }

    private suspend fun save(
        fileName: String,
        header: String,
        rows: List<String>
    ) {
        val file = File(cacheDirectory, fileName)
        val snapshotLock = snapshotLocks.computeIfAbsent(file.absolutePath) { Mutex() }

        snapshotLock.withLock {
            writer.write(
                file = file,
                header = header,
                rows = rows
            )
        }
    }

    private fun toCsvRow(vararg values: String): String =
        values.joinToString(",") { value ->
            if (value.containsSpecialCsvChars()) {
                "\"${value.replace("\"", "\"\"")}\""
            } else {
                value
            }
        }
}