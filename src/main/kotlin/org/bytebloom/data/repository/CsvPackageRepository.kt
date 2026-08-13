package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadPackages
import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.domain.repository.PackageRepository

class CsvPackageRepository : PackageRepository {
    override fun getAllPackages(): List<PackageRaw> {
        return loadPackages()
    }
}