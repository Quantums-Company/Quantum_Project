package org.bytebloom.data.source.csv

import org.bytebloom.data.raw.PackageRaw

interface PackageDataSource  {
    suspend fun loadAll(): List<PackageRaw>
    suspend fun getById(): List<PackageRaw>
    suspend fun create(): List<PackageRaw>
    suspend fun update(): List<PackageRaw>
    suspend fun delete(): List<PackageRaw>
}