package org.bytebloom.domain.repository

import org.bytebloom.data.raw.PackageRaw

interface PackageRepository {
    fun getAllPackages(): List<PackageRaw>
}