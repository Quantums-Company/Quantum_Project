package org.bytebloom.domain.dispatch

enum class DispatchRejectionReason {
    NO_PACKAGES,
    PACKAGE_NOT_IN_QUEUE,
    VEHICLE_NOT_STATIONED,
    PRIORITY_NOT_ALLOWED,
    CAPACITY_EXCEEDED
}