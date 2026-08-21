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
    private val packages = packages.toList()
    private val vehicles = vehicles.toList()

    private val circleSize =
        maxOf(MIN_RING_SIZE, vehicles.size * VEHICLE_SLOT_MULTIPLIER)

    private val _vehicleRing = mutableMapOf<Int, Vehicle>()
    val vehicleRing: Map<Int, Vehicle>
        get() = _vehicleRing

    private val _packageSlots = mutableMapOf<Package, Int>()
    val packageSlots: Map<Package, Int>
        get() = _packageSlots

    private val _assignments = mutableMapOf<Vehicle, MutableList<Package>>()
    val assignments: Map<Vehicle, List<Package>>
        get() = _assignments

    init {
        mapVehiclesToSlots()
        mapPackagesToSlots()
        distributeAllPackages()
    }

    private fun generateVehicleSlots(): List<Int> {

        if (vehicles.isEmpty()) {
            Logger.warning("At least one vehicle is required. No vehicle slots will be generated.")
            return emptyList()
        }

        val step = circleSize / vehicles.size

        return vehicles.indices.map {
            it * step
        }
    }

    private fun mapVehiclesToSlots() {
        generateVehicleSlots()
            .zip(vehicles)
            .forEach { (slot, vehicle) ->
                _vehicleRing[slot] = vehicle
            }
    }

    fun mapPackageToSlot(packageId: String): Int {
        return abs(packageId.hashCode()) % circleSize
    }

    private fun mapPackagesToSlots() {
        packages.forEach { pkg ->
            _packageSlots[pkg] = mapPackageToSlot(pkg.id)
        }
    }

    fun resolveVehicleClockwise(packageSlot: Int): Vehicle?{
        if (_vehicleRing.isEmpty()) {
            Logger.warning("Vehicle ring cannot be empty.")
            return null
        }
        return _vehicleRing[packageSlot]
            ?: findNextClockwiseVehicle(packageSlot)
    }

    private fun findNextClockwiseVehicle(packageSlot: Int): Vehicle {
        val sortedSlots = _vehicleRing.keys.sorted()
        val nextSlot = sortedSlots.firstOrNull { it > packageSlot } ?: sortedSlots.first()
        return _vehicleRing.getValue(nextSlot)
    }

    private fun distributeAllPackages() {
        for (vehicle in _vehicleRing.values) {
            _assignments[vehicle] = mutableListOf()
        }

        for ((pkg, slot) in _packageSlots) {
            val targetVehicle = resolveVehicleClockwise(slot)
            if (targetVehicle != null) {
                _assignments.getValue(targetVehicle).add(pkg)
            }
        }
    }

    fun removeVehicle(slot: Int): Boolean {
        val brokenVehicle =
            _vehicleRing.remove(slot)
                ?: return false

        reroutePackages(brokenVehicle)
        return true
    }

    private fun reroutePackages(brokenVehicle: Vehicle) {
        val orphanedPackages = _assignments[brokenVehicle] ?: mutableListOf()
        _assignments.remove(brokenVehicle)

        for (pkg in orphanedPackages) {
            val slot = requireNotNull(_packageSlots[pkg])
            val nextVehicle = resolveVehicleClockwise(slot)
            if (nextVehicle != null) {
                _assignments
                    .getOrPut(nextVehicle) { mutableListOf() }
                    .add(pkg)
            } else {
                Logger.warning(
                    "No vehicle available to reroute package ${pkg.id}."
                )
            }        }
    }

    fun captureSnapshot(): Map<Int, List<String>> =
        _vehicleRing.keys
            .sorted().associateWith { slot ->
                _vehicleRing[slot]
                    ?.let { vehicle ->
                        _assignments[vehicle]
                            ?.map(Package::id)
                            ?.sorted()
                    }
                    ?: emptyList()
            }
}