package org.bytebloom.data.repository.training

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Region
import org.bytebloom.domain.model.knn.CargoFeatures
import org.bytebloom.domain.model.knn.LabeledCargoRecord
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.TrainingCargoRepository

class PackageHistoryTrainingCargoRepository(
    private val packageRepository: PackageRepository
) : TrainingCargoRepository {

    override suspend fun getTrainingRecords(): List<LabeledCargoRecord> =
        packageRepository.getAll().mapNotNull { pkg -> pkg.toLabeledRecord() }

    private fun Package.toLabeledRecord(): LabeledCargoRecord? {
        val sector = regionOf(destinationWarehouse.regionalZone) ?: return null
        val features = CargoFeatures(
            latitude = destinationWarehouse.latitude,
            longitude = destinationWarehouse.longitude,
            weightKg = weight
        )
        return LabeledCargoRecord(features, sector)
    }

    private fun regionOf(regionalZone: String): Region? =
        Region.entries.find { region -> region.name.equals(regionalZone.trim(), ignoreCase = true) }
}