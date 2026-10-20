package org.bytebloom.logiroute.domain.usecase.classification

import org.bytebloom.logiroute.domain.knn.KNearestNeighborsClassifier
import org.bytebloom.logiroute.domain.knn.toCargoFeatures
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.repository.TrainingCargoRepository

class ClassifyPackageSectorUseCase(
    private val trainingCargoRepository: TrainingCargoRepository,
    private val classifier: KNearestNeighborsClassifier
) {

    suspend operator fun invoke(incomingPackage: Package): Region {
        val trainingRecords = trainingCargoRepository.getTrainingRecords()
        return classifier.classify(incomingPackage.toCargoFeatures(), trainingRecords)
    }
}