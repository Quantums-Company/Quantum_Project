package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadPackages
import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.domain.repository.PackageRepository

class CsvPackageRepository(private val fileName: String = "packages.csv") : PackageRepository {
    override fun getAllPackages(): List<PackageRaw> {
        return loadPackages(fileName)
    }
}