package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Route

interface RouteRepository {
    suspend fun getAll(): List<Route>
    suspend fun getById(id: String): Route?
    suspend fun create(route: Route): Route
    suspend fun update(route: Route): Route
    suspend fun delete(id: String): Boolean
}