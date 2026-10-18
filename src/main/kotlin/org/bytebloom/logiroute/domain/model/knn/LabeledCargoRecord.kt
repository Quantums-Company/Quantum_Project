package org.bytebloom.logiroute.domain.model.knn

import org.bytebloom.logiroute.domain.model.Region

data class LabeledCargoRecord(
    val features: CargoFeatures,
    val sector: Region
)