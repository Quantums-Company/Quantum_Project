package org.bytebloom.domain.usecase.commands

import org.bytebloom.domain.dispatch.BaseDispatchProcessor
import org.bytebloom.domain.dispatch.DispatchMode
import org.bytebloom.domain.dispatch.DispatchOrder
import org.bytebloom.domain.dispatch.DispatchOutcome
import org.bytebloom.domain.dispatch.ExpressDispatchProcessor
import org.bytebloom.domain.dispatch.StandardDispatchProcessor

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