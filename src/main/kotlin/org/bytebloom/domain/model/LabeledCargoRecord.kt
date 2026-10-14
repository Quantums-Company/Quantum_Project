package org.bytebloom.domain.model

data class LabeledCargoRecord(
    val features: CargoFeatures,
    val sector: Region
)