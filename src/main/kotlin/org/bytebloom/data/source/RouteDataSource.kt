package org.bytebloom.data.source

import org.bytebloom.data.raw.RouteRaw

interface RouteDataSource {
    fun loadAll(): List<RouteRaw>
}