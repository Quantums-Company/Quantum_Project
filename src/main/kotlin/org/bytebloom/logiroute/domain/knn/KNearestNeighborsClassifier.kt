package org.bytebloom.logiroute.domain.knn

import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures
import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.InvalidConfigurationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField

class KNearestNeighborsClassifier(
    private val distanceMetric: DistanceMetric = EuclideanDistance(),
    private val voteSelector: MajorityVoteSelector = MajorityVoteSelector(),
    private val neighborCount: Int = DEFAULT_NEIGHBOR_COUNT
) {
    init {
        if (neighborCount <= 0) {
            throw InvalidConfigurationException(
                "Neighbor count must be greater than zero."
            )
        }
    }

    fun classify(
        target: CargoFeatures,
        trainingRecords: List<LabeledCargoRecord>
    ): Region {
        if (trainingRecords.isEmpty()) {
            throw EntityValidationException(
                listOf(
                    ValidatorError.Custom(
                        ValidatorField.ENTITY,
                        "At least one training record is required for classification."
                    )
                )
            )
        }

        val normalizer = FeatureNormalizer(trainingRecords.map { it.features })
        val nearestNeighbors = findNearestNeighbors(
            normalizer.normalize(target),
            trainingRecords,
            normalizer
        )

        return voteSelector.select(nearestNeighbors)
    }

    private fun findNearestNeighbors(
        normalizedTarget: CargoFeatures,
        trainingRecords: List<LabeledCargoRecord>,
        normalizer: FeatureNormalizer
    ): List<LabeledCargoRecord> =
        trainingRecords
            .map { record -> record to distanceMetric.between(normalizedTarget, normalizer.normalize(record.features)) }
            .sortedBy { (_, distance) -> distance }
            .take(neighborCount)
            .map { (record, _) -> record }

    companion object {
        const val DEFAULT_NEIGHBOR_COUNT = 5
    }
}