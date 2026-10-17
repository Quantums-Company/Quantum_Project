package org.bytebloom.logiroute.data.source.remote

interface RemoteDataSource<Response, Request> {
    suspend fun loadAll(): List<Response>
    suspend fun loadById(id: String): Response?
    suspend fun create(request: Request): Response?
    suspend fun update(id: String, request: Request): Response?
    suspend fun delete(id: String): Boolean
}