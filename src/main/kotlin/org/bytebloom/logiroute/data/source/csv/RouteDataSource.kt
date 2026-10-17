package org.bytebloom.logiroute.data.source.csv

import org.bytebloom.logiroute.data.raw.RouteRaw

interface RouteDataSource {
    suspend fun loadAll(): List<RouteRaw>
    suspend fun getById(): List<RouteRaw>
    suspend fun create(): List<RouteRaw>
    suspend fun update(): List<RouteRaw>
    suspend fun delete(): List<RouteRaw>
}