package org.bytebloom.logiroute.data.source.remote

import org.bytebloom.logiroute.data.remote.dto.warehouseDto.WarehouseRequestDto
import org.bytebloom.logiroute.data.remote.dto.warehouseDto.WarehouseResponseDto

interface WarehouseRemoteDataSource : RemoteDataSource<WarehouseResponseDto, WarehouseRequestDto>