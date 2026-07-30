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
    var greaterIndex = low - 1

    for (index in low until high){
        if (packages[index].weight >= pivot){
            greaterIndex++

            val swappedPackage = packages[greaterIndex]
            packages[greaterIndex] = packages[index]
            packages[index] = swappedPackage
        }
    }
    val swappedPackage = packages[greaterIndex + 1]
    packages[greaterIndex + 1] = packages[high]
    packages[high] = swappedPackage

    return greaterIndex + 1

}





