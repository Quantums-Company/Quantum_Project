package org.bytebloom.logiroute.domain.knn

import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord
import org.bytebloom.logiroute.domain.model.exception.InvalidConfigurationException

class MajorityVoteSelector {
    fun select(nearestNeighborsByDistance: List<LabeledCargoRecord>): Region {
        if (nearestNeighborsByDistance.isEmpty()) {
            throw InvalidConfigurationException(
                "Majority voting requires at least one neighbor."
            )
        }

        val votesBySector = nearestNeighborsByDistance
            .groupingBy { it.sector }
            .eachCount()

        val highestVoteCount = votesBySector.values.max()

        return nearestNeighborsByDistance
            .first { neighbor ->
                votesBySector.getValue(neighbor.sector) == highestVoteCount
            }
            .sector
    }
}