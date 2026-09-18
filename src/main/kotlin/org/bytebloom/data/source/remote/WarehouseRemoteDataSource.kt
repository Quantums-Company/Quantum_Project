package org.bytebloom.data.source.remote

import org.bytebloom.data.remote.dto.WarehouseRequestDto
import org.bytebloom.data.remote.dto.warehouseDto.WarehouseResponseDto

interface WarehouseRemoteDataSource : RemoteDataSource<WarehouseResponseDto, WarehouseRequestDto>