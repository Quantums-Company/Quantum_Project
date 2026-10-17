package org.bytebloom.logiroute.data.csv.datasource

import org.bytebloom.logiroute.data.csv.raw.PackageRaw

interface PackageDataSource  {
    suspend fun loadAll(): List<PackageRaw>
}