package org.bytebloom.logiroute.data.remote.datasource

import org.bytebloom.logiroute.data.remote.dto.PackageRequestDto
import org.bytebloom.logiroute.data.remote.dto.PackageResponseDto

interface PackageRemoteDataSource : RemoteDataSource<PackageResponseDto, PackageRequestDto>