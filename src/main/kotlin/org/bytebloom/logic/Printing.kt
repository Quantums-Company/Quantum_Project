package org.bytebloom.logic

import org.bytebloom.dataHolder.packageRaw

fun printTopPackages(packages: List<packageRaw>, count: Int) {
    println("\nTop Priority Packages:\n")

    packages.take(count).forEachIndexed { index, packageItem ->
        println("${index + 1}.")
        println("ID: ${packageItem.id}")
        println("Weight: ${packageItem.weight}")
        println("Origin: ${packageItem.originHubId}")
        println("Priority: ${packageItem.priority}")
        println("Destination: ${packageItem.destinationHubId}")
        println()
    }
}