package org.bytebloom.data.source.remote

import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.dto.vehicleDto.VehicleResponseDto

interface VehicleRemoteDataSource : RemoteDataSource<VehicleResponseDto, VehicleRequestDto>