package org.bytebloom.logiroute.data.csv.cache

import org.bytebloom.logiroute.data.csv.io.CsvFileWriter
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.data.csv.io.CsvTable
import java.io.File

class CsvSnapshotStore(
    private val writer: CsvFileWriter,
    private val cacheDirectory: File = File("src/resources/cache")
) {
    companion object {
        private val SPECIAL_CSV_CHARS = charArrayOf(',', '"', '\n', '\r')
    }

    private fun String.containsSpecialCsvChars(): Boolean =
        any { it in SPECIAL_CSV_CHARS }

    fun saveWarehouses(items: List<Warehouse>) {
        save(
            CsvTable.WAREHOUSES.fileName,
            "id,name,regionalZone,latitude,longitude",
            items.map {
                row(
                    it.id, it.name, it.regionalZone,
                    it.latitude.toString(), it.longitude.toString()
                )
            }
        )
    }

    fun saveRoutes(items: List<Route>) {
        save(
            CsvTable.ROUTES.fileName,
            "routeId,originHubId,destinationHubId,distanceKm,typicalDelayMin",
            items.map {
                row(
                    it.id, it.originWarehouse.id, it.destinationWarehouse.id,
                    it.distanceKm.toString(), it.typicalDelayMin.toString()
                )
            }
        )
    }

    fun saveVehicles(items: List<Vehicle>) {
        save(
            CsvTable.VEHICLES.fileName,
            "vehicleId,currentHubId,maxCapacityKg,costPerKm",
            items.map {
                row(
                    it.id, it.currentWarehouse.id,
                    it.maxCapacityKg.toString(), it.costPerKm.toString()
                )
            }
        )
    }

    fun savePackages(items: List<Package>) {
        save(
            CsvTable.PACKAGES.fileName,
            "id,weight,originHubId,destinationHubId,priority",
            items.map {
                row(
                    it.id, it.weight.toString(),
                    it.originWarehouse.id, it.destinationWarehouse.id,
                    it.priority.name
                )
            }
        )
    }

    private fun save(
        fileName: String,
        header: String,
        rows: List<String>
    ) {
        writer.write(
            file = File(cacheDirectory, fileName),
            header = header,
            rows = rows
        )
    }

    private fun row(vararg values: String): String =
        values.joinToString(",") { value ->
            if (value.containsSpecialCsvChars()) {
                "\"${value.replace("\"", "\"\"")}\""
            } else {
                value
            }
        }
}