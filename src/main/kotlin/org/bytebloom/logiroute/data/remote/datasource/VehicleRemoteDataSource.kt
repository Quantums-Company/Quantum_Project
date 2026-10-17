package org.bytebloom.logiroute.data.remote.datasource

import org.bytebloom.logiroute.data.remote.dto.VehicleRequestDto
import org.bytebloom.logiroute.data.remote.dto.VehicleResponseDto

interface VehicleRemoteDataSource : RemoteDataSource<VehicleResponseDto, VehicleRequestDto>