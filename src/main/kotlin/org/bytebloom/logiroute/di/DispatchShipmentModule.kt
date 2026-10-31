package org.bytebloom.logiroute.di

import org.bytebloom.logiroute.domain.dispatch.DispatchNotifier
import org.bytebloom.logiroute.domain.dispatch.DispatchOutcome
import org.bytebloom.logiroute.domain.dispatch.ExpressDispatchProcessor
import org.bytebloom.logiroute.domain.dispatch.ShipmentStateUpdater
import org.bytebloom.logiroute.domain.dispatch.StandardDispatchProcessor
import org.bytebloom.logiroute.domain.shipment.ShipmentTracker
import org.bytebloom.logiroute.domain.tree.hub.HubTreeBuilder
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchCargoUseCase
import org.bytebloom.logiroute.domain.usecase.shipment.AdvanceShipmentStateUseCase
import org.koin.dsl.module

/**
 * Registrations the "all screens" part of the application needs and the other modules do not have:
 * the dispatch processors, the shipment tracker, the hub tree builder and the shipment state use case.
 */
val dispatchShipmentModule = module {
    single { ShipmentTracker() }
    single<ShipmentStateUpdater> { get<ShipmentTracker>() }

    // The terminal has nobody to notify; an app can replace this with a real notifier.
    single<DispatchNotifier> {
        object : DispatchNotifier {
            override fun notify(outcome: DispatchOutcome) {
                // No-op implementation for terminal UI
            }
        }
    }

    factory { StandardDispatchProcessor(get()) }
    factory { ExpressDispatchProcessor(get(), get()) }
    factory { DispatchCargoUseCase(get(), get()) }
    factory { AdvanceShipmentStateUseCase() }
    factory { HubTreeBuilder(get()) }
}
