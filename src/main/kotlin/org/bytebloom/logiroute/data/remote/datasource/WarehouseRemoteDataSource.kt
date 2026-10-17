package org.bytebloom.logiroute.data.remote.datasource

import org.bytebloom.logiroute.data.remote.dto.WarehouseRequestDto
import org.bytebloom.logiroute.data.remote.dto.WarehouseResponseDto

interface WarehouseRemoteDataSource : RemoteDataSource<WarehouseResponseDto, WarehouseRequestDto>