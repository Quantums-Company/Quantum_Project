package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Route

interface RouteRepository {
    fun getAll(): List<Route>
    fun getById(id: String): Route?
    fun create(route: Route): Route
    fun update(route: Route): Route
    fun delete(id: String): Boolean
}