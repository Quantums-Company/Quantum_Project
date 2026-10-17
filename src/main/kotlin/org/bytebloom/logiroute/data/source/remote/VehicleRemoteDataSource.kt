package org.bytebloom.logiroute.data.source.remote

import org.bytebloom.logiroute.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.logiroute.data.remote.dto.vehicleDto.VehicleResponseDto

interface VehicleRemoteDataSource : RemoteDataSource<VehicleResponseDto, VehicleRequestDto>