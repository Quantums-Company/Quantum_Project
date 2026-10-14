package org.bytebloom.domain.knn

import org.bytebloom.domain.model.Region
import org.bytebloom.domain.model.knn.CargoFeatures
import org.bytebloom.domain.model.knn.LabeledCargoRecord

class KNearestNeighborsClassifier(
    private val distanceMetric: DistanceMetric = EuclideanDistance(),
    private val voteSelector: MajorityVoteSelector = MajorityVoteSelector(),
    private val neighborCount: Int = DEFAULT_NEIGHBOR_COUNT
) {
    init {
        require(neighborCount > 0) { "Neighbor count must be positive" }
    }

    fun classify(target: CargoFeatures, trainingRecords: List<LabeledCargoRecord>): Region {
        require(trainingRecords.isNotEmpty()) { "Training records must not be empty" }
        val normalizer = FeatureNormalizer(trainingRecords.map { it.features })
        val nearestNeighbors = findNearestNeighbors(normalizer.normalize(target), trainingRecords, normalizer)
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