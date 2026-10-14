package org.bytebloom.domain.model.knn

import org.bytebloom.domain.model.Region

data class LabeledCargoRecord(
    val features: CargoFeatures,
    val sector: Region
)