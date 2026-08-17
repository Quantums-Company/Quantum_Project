package org.bytebloom.domain.sorting

import org.bytebloom.domain.model.Priority
import org.bytebloom.data.raw.PackageRaw

private fun getPriorityValue(priority: Priority): Int {
    return when (priority) {
        Priority.URGENT -> 3
        Priority.STANDARD -> 2
        Priority.LOW -> 1
    }
}

private fun hasHigherPriority(first: PackageRaw, second: PackageRaw): Boolean {
    return getPriorityValue(first.priority) > getPriorityValue(second.priority)
}

private fun hasGreaterWeight(first: PackageRaw, second: PackageRaw): Boolean {
    return first.weight > second.weight
}

private fun isMoreUrgentThan(first: PackageRaw, second: PackageRaw): Boolean {
    if (first.priority != second.priority) {
        return hasHigherPriority(first, second)
    }

    return hasGreaterWeight(first, second)
}

//Urgency is defined as the highest priority first,
//followed by the greater weight (descending) as a secondary rule.
fun selectionSortPackagesByUrgency(packages: MutableList<PackageRaw>) {
    for (i in 0 until packages.size - 1) {
        var bestPackageIndex = i

        for (j in i + 1 until packages.size) {
            if (isMoreUrgentThan(packages[j], packages[bestPackageIndex])) {
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