package org.bytebloom.domain.usecase.queries.classification

import org.bytebloom.domain.knn.KNearestNeighborsClassifier
import org.bytebloom.domain.knn.toCargoFeatures
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Region
import org.bytebloom.domain.repository.TrainingCargoRepository

class ClassifyPackageSectorUseCase(
    private val trainingCargoRepository: TrainingCargoRepository,
    private val classifier: KNearestNeighborsClassifier
) {

    suspend operator fun invoke(incomingPackage: Package): Region {
        val trainingRecords = trainingCargoRepository.getTrainingRecords()
        return classifier.classify(incomingPackage.toCargoFeatures(), trainingRecords)
    }
}