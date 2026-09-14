package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Package

interface PackageRepository {
    fun getAll(): List<Package>
    fun getById(id: String): Package?
    fun create(pkg: Package): Package
    fun update(pkg: Package): Package
    fun delete(id: String): Boolean
}