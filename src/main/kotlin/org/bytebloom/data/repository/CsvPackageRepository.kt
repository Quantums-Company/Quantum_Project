package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadPackages
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository

class CsvPackageRepository : PackageRepository {
    override fun getAllPackages(): List<Package> {
        return loadPackages()
    }
}