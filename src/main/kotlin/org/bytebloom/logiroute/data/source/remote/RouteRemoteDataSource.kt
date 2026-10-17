package org.bytebloom.logiroute.data.source.remote

import org.bytebloom.logiroute.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.logiroute.data.remote.dto.routeDto.RouteResponseDto

interface RouteRemoteDataSource : RemoteDataSource<RouteResponseDto, RouteRequestDto>