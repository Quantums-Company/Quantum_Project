package org.bytebloom.app

import org.bytebloom.logic.printTopPackages
import org.bytebloom.readers.readPackages
import org.bytebloom.logic.selectionSortPackagesByUrgency

fun main() {
    val packages = readPackages("packages.csv").toMutableList()

    selectionSortPackagesByUrgency(packages)

    printTopPackages(packages, 3)
}