package org.bytebloom.domain.repository

import org.bytebloom.data.raw.RouteRaw

interface RouteRepository {
    fun getAllRoutes(): List<RouteRaw>
}