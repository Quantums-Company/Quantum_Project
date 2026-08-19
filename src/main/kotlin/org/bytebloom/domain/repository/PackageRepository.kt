package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Package

interface PackageRepository {
    fun getAll(): List<Package>
}