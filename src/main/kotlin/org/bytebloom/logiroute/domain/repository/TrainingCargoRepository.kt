package org.bytebloom.logiroute.domain.repository

import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord

interface TrainingCargoRepository {
    suspend fun getTrainingRecords(): List<LabeledCargoRecord>
}