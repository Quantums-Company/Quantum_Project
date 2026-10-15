package org.bytebloom.data.repository.training

import org.bytebloom.domain.model.Region
import org.bytebloom.domain.model.knn.CargoFeatures
import org.bytebloom.domain.model.knn.LabeledCargoRecord
import org.bytebloom.domain.repository.TrainingCargoRepository

class LocalTrainingCargoRepository : TrainingCargoRepository {

    override suspend fun getTrainingRecords(): List<LabeledCargoRecord> =
        listOf(
            LabeledCargoRecord(
                CargoFeatures(32.22, 35.95, 10.0),
                Region.NORTH
            ),
            LabeledCargoRecord(
                CargoFeatures(32.23, 35.96, 15.0),
                Region.NORTH
            ),
            LabeledCargoRecord(
                CargoFeatures(32.20, 35.93, 20.0),
                Region.SOUTH
            ),
            LabeledCargoRecord(
                CargoFeatures(32.19, 35.92, 5.0),
                Region.SOUTH
            ),
            LabeledCargoRecord(
                CargoFeatures(31.90, 35.20, 12.0),
                Region.SOUTH
            )
        )
}
