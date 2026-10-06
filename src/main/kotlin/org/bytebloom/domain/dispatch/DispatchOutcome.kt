package org.bytebloom.domain.dispatch

sealed class DispatchOutcome {
    abstract val order: DispatchOrder

    data class Dispatched(
        override val order: DispatchOrder
    ) : DispatchOutcome()

    data class Rejected(
        override val order: DispatchOrder,
        val reason: DispatchRejectionReason
    ) : DispatchOutcome()
}