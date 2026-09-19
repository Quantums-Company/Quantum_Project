package org.bytebloom.data.source.remote

import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.dto.packageDto.PackageResponseDto

interface PackageRemoteDataSource : RemoteDataSource<PackageResponseDto, PackageRequestDto>