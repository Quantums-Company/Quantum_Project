package org.bytebloom.logiroute.domain.dispatch

fun interface DispatchNotifier {
    fun notify(outcome: DispatchOutcome)
}