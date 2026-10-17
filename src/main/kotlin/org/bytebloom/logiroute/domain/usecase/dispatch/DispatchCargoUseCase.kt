package org.bytebloom.logiroute.domain.usecase.dispatch

import org.bytebloom.logiroute.domain.dispatch.BaseDispatchProcessor
import org.bytebloom.logiroute.domain.dispatch.DispatchMode
import org.bytebloom.logiroute.domain.dispatch.DispatchOrder
import org.bytebloom.logiroute.domain.dispatch.DispatchOutcome
import org.bytebloom.logiroute.domain.dispatch.ExpressDispatchProcessor
import org.bytebloom.logiroute.domain.dispatch.StandardDispatchProcessor

class DispatchCargoUseCase(
    private val standardProcessor: StandardDispatchProcessor,
    private val expressProcessor: ExpressDispatchProcessor
) {
    operator fun invoke(order: DispatchOrder, mode: DispatchMode): DispatchOutcome =
        processorFor(mode).dispatch(order)

    private fun processorFor(mode: DispatchMode): BaseDispatchProcessor = when (mode) {
        DispatchMode.STANDARD -> standardProcessor
        DispatchMode.EXPRESS -> expressProcessor
    }
}