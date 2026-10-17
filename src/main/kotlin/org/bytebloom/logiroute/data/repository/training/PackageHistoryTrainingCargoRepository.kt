package org.bytebloom.logiroute.data.repository.training

import org.bytebloom.logiroute.domain.knn.toCargoFeatures
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.TrainingCargoRepository

class PackageHistoryTrainingCargoRepository(
    private val packageRepository: PackageRepository
) : TrainingCargoRepository {

    override suspend fun getTrainingRecords(): List<LabeledCargoRecord> =
        packageRepository.getAll().mapNotNull { pkg -> pkg.toLabeledRecord() }

    private fun Package.toLabeledRecord(): LabeledCargoRecord? {
        val sector = regionOf(destinationWarehouse.regionalZone) ?: return null
        return LabeledCargoRecord(toCargoFeatures(), sector)
    }

    private fun regionOf(regionalZone: String): Region? =
        Region.entries.find { region -> region.name.equals(regionalZone.trim(), ignoreCase = true) }
}