package org.bytebloom.logiroute.domain.dispatch

interface DispatchNotifier {
    fun notify(outcome: DispatchOutcome)
}