package org.bytebloom.domain.knn

import org.bytebloom.domain.model.Region
import org.bytebloom.domain.model.knn.LabeledCargoRecord

class MajorityVoteSelector {
    fun select(nearestNeighborsByDistance: List<LabeledCargoRecord>): Region {
        require(nearestNeighborsByDistance.isNotEmpty()) { "At least one neighbor is required to vote" }
        val votesBySector = nearestNeighborsByDistance.groupingBy { it.sector }.eachCount()
        val highestVoteCount = votesBySector.values.max()
        return nearestNeighborsByDistance
            .first { neighbor -> votesBySector.getValue(neighbor.sector) == highestVoteCount }
            .sector
    }
}