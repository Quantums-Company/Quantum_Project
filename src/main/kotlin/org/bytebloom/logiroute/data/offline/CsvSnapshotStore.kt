package org.bytebloom.logiroute.data.offline

import org.bytebloom.logiroute.data.local.cleaner.CsvWriter
import org.bytebloom.logiroute.data.local.common.CsvTablesName
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import java.io.File

class CsvSnapshotStore(
    private val writer: CsvWriter,
    private val cacheDirectory: File = File("src/resources/cache")
) {
    fun saveWarehouses(items: List<Warehouse>) {
        save(
            CsvTablesName.WAREHOUSE,
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
            CsvTablesName.ROUTE,
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
            CsvTablesName.FLEET,
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
            CsvTablesName.PACKAGE,
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
            if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
                "\"${value.replace("\"", "\"\"")}\""
            } else {
                value
            }
        }
}