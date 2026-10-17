package org.bytebloom.logiroute.data.source.csv

import org.bytebloom.logiroute.data.raw.PackageRaw

interface PackageDataSource  {
    suspend fun loadAll(): List<PackageRaw>
    suspend fun getById(): List<PackageRaw>
    suspend fun create(): List<PackageRaw>
    suspend fun update(): List<PackageRaw>
    suspend fun delete(): List<PackageRaw>
}