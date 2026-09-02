package org.bytebloom.domain.vehicleReshuffling

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.usecase.queries.planing.CargoRecoveryPlan
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
        get() = _vehicleRing.toMap()

    private val _packageSlots = mutableMapOf<Package, Int>()

    private val _assignments =
        mutableMapOf<Vehicle, MutableList<Package>>()

    val assignments: Map<Vehicle, List<Package>>
        get() = _assignments.mapValues { (_, packages) ->
            packages.toList()
        }

    init {
        mapVehiclesToSlots()
        mapPackagesToSlots()
        distributeAllPackages()
    }

    fun findSlotForVehicle(vehicleId: String): Int? =
        _vehicleRing.entries
            .firstOrNull { (_, vehicle) ->
                vehicle.id.equals(vehicleId, ignoreCase = true)
            }
            ?.key

    fun getAssignedPackages(
        vehicle: Vehicle
    ): List<Package> =
        _assignments[vehicle].orEmpty().toList()

    fun findVehicleForPackage(
        packageId: String
    ): Vehicle? =
        _assignments.entries
            .firstOrNull { (_, packages) ->
                packages.any {
                    it.id.equals(packageId, ignoreCase = true)
                }
            }
            ?.key

    fun removeVehicle(slot: Int): Boolean {
        val removedVehicle =
            _vehicleRing.remove(slot)
                ?: return false

        reroutePackages(removedVehicle)

        return true
    }

    fun createRecoveryPlan(
        failedVehicle: Vehicle
    ): CargoRecoveryPlan? {

        val failedSlot =
            findSlotForVehicle(failedVehicle.id)
                ?: run {
                    Logger.warning(
                        "Failed vehicle '${failedVehicle.id}' " +
                                "does not exist in the hashing ring."
                    )
                    return null
                }

        val affectedPackages =
            getAssignedPackages(failedVehicle)

        val healthyVehicles =
            _vehicleRing.values.filterNot {
                it.id.equals(
                    failedVehicle.id,
                    ignoreCase = true
                )
            }

        if (healthyVehicles.isEmpty()) {
            Logger.warning(
                "No healthy vehicle is available to recover " +
                        "cargo from vehicle '${failedVehicle.id}'."
            )
            return null
        }

        removeVehicle(failedSlot)

        val assignments =
            affectedPackages.mapNotNull { pkg ->
                findVehicleForPackage(pkg.id)?.let { vehicle ->
                    pkg.id to vehicle.id
                }
            }.toMap()

        return CargoRecoveryPlan(
            failedVehicleId = failedVehicle.id,
            rescueVehicleByPackageId = assignments
        )
    }

    private fun generateVehicleSlots(): List<Int> {

        if (vehiclesList.isEmpty()) {
            Logger.warning(
                "At least one vehicle is required. " +
                        "No vehicle slots generated."
            )
            return emptyList()
        }

        val step = circleSize / vehiclesList.size

        return vehiclesList.indices
            .map { index -> index * step }
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

        if (orphanedPackages.isEmpty()) {
            return
        }

        orphanedPackages.forEach { pkg ->

            val packageSlot =
                _packageSlots[pkg]

            if (packageSlot == null) {
                Logger.warning(
                    "No hash slot found for package '${pkg.id}'."
                )
                return@forEach
            }

            val nextVehicle =
                resolveVehicleClockwise(packageSlot)

            if (nextVehicle == null) {
                Logger.warning(
                    "No vehicle available to reroute " +
                            "package '${pkg.id}'."
                )
                return@forEach
            }

            _assignments
                .getOrPut(nextVehicle) {
                    mutableListOf()
                }
                .add(pkg)
        }
    }
}