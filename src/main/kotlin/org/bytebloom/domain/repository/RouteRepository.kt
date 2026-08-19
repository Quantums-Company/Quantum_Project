package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Route

interface RouteRepository {
    fun getAll(): List<Route>
}