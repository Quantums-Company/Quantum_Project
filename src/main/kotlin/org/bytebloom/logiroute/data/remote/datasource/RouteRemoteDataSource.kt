package org.bytebloom.logiroute.data.remote.datasource

import org.bytebloom.logiroute.data.remote.dto.RouteRequestDto
import org.bytebloom.logiroute.data.remote.dto.RouteResponseDto

interface RouteRemoteDataSource : RemoteDataSource<RouteResponseDto, RouteRequestDto>