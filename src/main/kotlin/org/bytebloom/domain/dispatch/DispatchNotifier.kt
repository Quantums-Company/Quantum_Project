package org.bytebloom.domain.dispatch

fun interface DispatchNotifier {
    fun notify(outcome: DispatchOutcome)
}