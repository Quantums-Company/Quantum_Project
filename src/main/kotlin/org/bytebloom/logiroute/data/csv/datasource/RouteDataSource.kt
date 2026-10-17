package org.bytebloom.logiroute.data.csv.datasource

import org.bytebloom.logiroute.data.csv.raw.RouteRaw

interface RouteDataSource {
    suspend fun loadAll(): List<RouteRaw>
}