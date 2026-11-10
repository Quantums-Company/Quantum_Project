package org.bytebloom.logiroute.domain.model.knn

import org.bytebloom.logiroute.domain.model.Region

data class LabeledCargoRecord(
    val features: CargoFeatures,
    val sector: Region,
    /** Id of the package this record was built from; lets a classification leave the target package out. */
    val sourcePackageId: String? = null
)