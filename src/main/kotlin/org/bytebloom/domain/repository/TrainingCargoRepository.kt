package org.bytebloom.domain.repository

import org.bytebloom.domain.model.knn.LabeledCargoRecord

interface TrainingCargoRepository {
    suspend fun getTrainingRecords(): List<LabeledCargoRecord>
}