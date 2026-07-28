package org.bytebloom.logic

import org.bytebloom.dataHolder.Priority
import org.bytebloom.dataHolder.packageRaw

private fun getPriorityValue(priority: Priority): Int {
    return when (priority) {
        Priority.URGENT -> 3
        Priority.STANDARD -> 2
        Priority.LOW -> 1
    }
}

private fun hasHigherPriority(first: packageRaw, second: packageRaw): Boolean {
    return getPriorityValue(first.priority) > getPriorityValue(second.priority)
}

private fun hasGreaterWeight(first: packageRaw, second: packageRaw): Boolean {
    return first.weight > second.weight
}

private fun shouldComeBefore(first: packageRaw, second: packageRaw): Boolean {
    if (first.priority != second.priority) {
        return hasHigherPriority(first, second)
    }

    return hasGreaterWeight(first, second)
}

fun selectionSortPackagesByPriorityAndWeight(packages: MutableList<packageRaw>) {
    for (i in 0 until packages.size - 1) {
        var bestPackageIndex = i

        for (j in i + 1 until packages.size) {
            if (shouldComeBefore(packages[j], packages[bestPackageIndex])) {
                bestPackageIndex = j
            }
        }

        if (bestPackageIndex != i) {
            val tempPackage = packages[i]
            packages[i] = packages[bestPackageIndex]
            packages[bestPackageIndex] = tempPackage
        }
    }
}

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