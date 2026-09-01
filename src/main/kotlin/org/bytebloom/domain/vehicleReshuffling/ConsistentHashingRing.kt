package org.bytebloom.domain.vehicleReshuffling

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import kotlin.math.abs
import  org.bytebloom.util.Logger

private const val MIN_RING_SIZE = 100
private const val VEHICLE_SLOT_MULTIPLIER = 2

class ConsistentHashingRing(
    packages: Collection<Package>,
    vehicles: Collection<Vehicle>
) {
    private val packagesList = packages.toList()
    private val vehiclesList = vehicles.toList()

    private val circleSize =
        maxOf(
            MIN_RING_SIZE,
            vehiclesList.size * VEHICLE_SLOT_MULTIPLIER
        )

    private val _vehicleRing = mutableMapOf<Int, Vehicle>()
    val vehicleRing: Map<Int, Vehicle>
        get() = _vehicleRing

    private val _packageSlots = mutableMapOf<Package, Int>()

    private val _assignments =
        mutableMapOf<Vehicle, MutableList<Package>>()

    val assignments: Map<Vehicle, List<Package>>
        get() = _assignments

    init {
        mapVehiclesToSlots()
        mapPackagesToSlots()
        distributeAllPackages()
    }

    fun findSlotForVehicle(vehicleId: String): Int? =
        _vehicleRing.entries
            .firstOrNull {
                it.value.id.equals(vehicleId, ignoreCase = true)
            }
            ?.key

    fun getAssignedPackages(vehicle: Vehicle): List<Package> =
        _assignments[vehicle].orEmpty()

    fun findVehicleForPackage(packageId: String): Vehicle =
        _assignments.entries
            .first { (_, packages) ->
                packages.any {
                    it.id.equals(packageId, ignoreCase = true)
                }
            }
            .key

    fun removeVehicle(slot: Int): Boolean {
        val removedVehicle =
            _vehicleRing.remove(slot)
                ?: return false

        reroutePackages(removedVehicle)

        return true
    }

    private fun generateVehicleSlots(): List<Int> {

        if (vehiclesList.isEmpty()) {
            Logger.warning(
                "At least one vehicle is required. " +
                        "No vehicle slots generated."
            )
            return emptyList()
        }

        val step =
            circleSize / vehiclesList.size

        return vehiclesList.indices
            .map { it * step }
    }

    private fun mapVehiclesToSlots() {
        generateVehicleSlots()
            .zip(vehiclesList)
            .forEach { (slot, vehicle) ->
                _vehicleRing[slot] = vehicle
            }
    }

    private fun mapPackagesToSlots() {
        packagesList.forEach { pkg ->
            _packageSlots[pkg] =
                abs(pkg.id.hashCode()) % circleSize
        }
    }

    private fun resolveVehicleClockwise(
        packageSlot: Int
    ): Vehicle? {

        if (_vehicleRing.isEmpty()) {
            Logger.warning(
                "Vehicle ring cannot be empty."
            )
            return null
        }

        return _vehicleRing[packageSlot]
            ?: findNextClockwiseVehicle(packageSlot)
    }

    private fun findNextClockwiseVehicle(
        packageSlot: Int
    ): Vehicle {

        val sortedSlots =
            _vehicleRing.keys.sorted()

        val nextSlot =
            sortedSlots.firstOrNull {
                it > packageSlot
            } ?: sortedSlots.first()

        return _vehicleRing.getValue(nextSlot)
    }

    private fun distributeAllPackages() {

        _vehicleRing.values.forEach { vehicle ->
            _assignments[vehicle] = mutableListOf()
        }

        _packageSlots.forEach { (pkg, slot) ->
            resolveVehicleClockwise(slot)
                ?.let { vehicle ->
                    _assignments
                        .getValue(vehicle)
                        .add(pkg)
                }
        }
    }

    private fun reroutePackages(
        brokenVehicle: Vehicle
    ) {

        val orphanedPackages =
            _assignments
                .remove(brokenVehicle)
                .orEmpty()

        orphanedPackages.forEach { pkg ->

            val slot =
                requireNotNull(_packageSlots[pkg])

            val nextVehicle =
                resolveVehicleClockwise(slot)

            if (nextVehicle != null) {
                _assignments
                    .getOrPut(nextVehicle) {
                        mutableListOf()
                    }
                    .add(pkg)
            } else {
                Logger.warning(
                    "No vehicle available to reroute " +
                            "package ${pkg.id}."
                )
            }
        }
    }
}