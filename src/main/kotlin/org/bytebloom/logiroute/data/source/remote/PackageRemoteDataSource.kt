package org.bytebloom.logiroute.data.source.remote

import org.bytebloom.logiroute.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.logiroute.data.remote.dto.packageDto.PackageResponseDto

interface PackageRemoteDataSource : RemoteDataSource<PackageResponseDto, PackageRequestDto>