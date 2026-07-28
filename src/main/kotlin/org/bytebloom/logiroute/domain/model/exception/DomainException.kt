package org.bytebloom.logiroute.domain.model.exception

sealed class DomainException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    companion object {
        const val WAREHOUSE_NOT_FOUND_MESSAGE = "No warehouse found near the specified coordinates."
        const val ROUTE_NOT_FOUND_MESSAGE = "Could not find a valid route between the specified locations."
    }
}

class WarehouseNotFoundException(
    message: String = WAREHOUSE_NOT_FOUND_MESSAGE
) : DomainException(message)

class RouteNotFoundException(
    message: String = ROUTE_NOT_FOUND_MESSAGE
) : DomainException(message)
