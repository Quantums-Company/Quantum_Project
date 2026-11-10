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
        // The package being classified may already be part of the history. It must not vote for itself,
        // otherwise it is its own nearest neighbour (distance 0) and the answer is always "correct".
        val trainingRecords = trainingCargoRepository.getTrainingRecords()
            .filterNot { record -> record.sourcePackageId == incomingPackage.id }
        return classifier.classify(incomingPackage.toCargoFeatures(), trainingRecords)
    }
}