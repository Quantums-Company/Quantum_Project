package org.bytebloom.domain.logic

import org.bytebloom.domain.model.Package

fun quickSortCargoByWeight(packages: MutableList<Package>) {
    val lastIndex = packages.size - 1
    sortRangeByWeight(packages, 0, lastIndex)
}

fun sortRangeByWeight(packages: MutableList<Package>, low: Int, high: Int) {
    if(low < high) {
        val pivotIndex = placePivotAndRearrange(packages, low, high)
        val indexBeforePivot = pivotIndex - 1
        sortRangeByWeight(packages, low, indexBeforePivot)
        val indexAfterPivot = pivotIndex + 1
        sortRangeByWeight(packages, indexAfterPivot, high)
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
    val correctPivotIndex = greaterIndex + 1
    val swappedPackage = packages[correctPivotIndex]
    packages[correctPivotIndex] = packages[high]
    packages[high] = swappedPackage

    return correctPivotIndex

}





