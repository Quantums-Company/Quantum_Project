package org.bytebloom.data.source.remote

import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteResponseDto

interface RouteRemoteDataSource : RemoteDataSource<RouteResponseDto, RouteRequestDto>