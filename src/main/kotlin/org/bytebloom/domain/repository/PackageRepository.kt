package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Package

interface PackageRepository {
    suspend fun getAll(): List<Package>
    suspend fun getById(id: String): Package?
    suspend fun create(pkg: Package): Package
    suspend fun update(pkg: Package): Package
    suspend fun delete(id: String): Boolean
}