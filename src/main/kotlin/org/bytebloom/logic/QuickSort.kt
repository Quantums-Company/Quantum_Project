package org.bytebloom.logic

import org.bytebloom.domain.model.Package

fun quickSortCargoByWeight(packages: MutableList<Package>) {
    sortRangeByWeight(packages, 0, packages.size - 1)
}

fun sortRangeByWeight(packages: MutableList<Package>, low: Int, high: Int) {
    if(low < high) {
        val pivotIndex = placePivotAndRearrange(packages, low, high)
        sortRangeByWeight(packages, low, pivotIndex - 1)
        sortRangeByWeight(packages, pivotIndex + 1, high)
    }
}

fun placePivotAndRearrange(packages: MutableList<Package>, low: Int, high: Int): Int {
    val pivot = packages[high].weight
    var greaterIndex = low

    for (index in low until high) {
        if (packages[index].weight >= pivot) {
            val temp = packages[index]

            for (k in index downTo greaterIndex + 1) {
                packages[k] = packages[k - 1]
            }

            packages[greaterIndex] = temp
            greaterIndex++
        }
    }

    val tempPivot = packages[high]
    for (k in high downTo greaterIndex + 1) {
        packages[k] = packages[k - 1]
    }
    packages[greaterIndex] = tempPivot

    return greaterIndex
}