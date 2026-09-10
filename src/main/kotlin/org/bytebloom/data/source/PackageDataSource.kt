package org.bytebloom.data.source

import org.bytebloom.data.raw.PackageRaw

interface PackageDataSource {
    fun loadAll(): List<PackageRaw>
}