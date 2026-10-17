package org.bytebloom.logiroute.data.remote.resilience

enum class CircuitState {
    CLOSED,
    OPEN,
    HALF_OPEN
}