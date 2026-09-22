package org.bytebloom.domain.validation

sealed class ValidationField {
    class Id : ValidationField()
    class Name : ValidationField()
    class RegionalZone : ValidationField()
    class Latitude : ValidationField()
    class Longitude : ValidationField()
    class Weight : ValidationField()
    class Priority : ValidationField()
    class DistanceKm : ValidationField()
    class TypicalDelayMin : ValidationField()
    class MaxCapacityKg : ValidationField()
    class CostPerKm : ValidationField()
    class OriginWarehouse : ValidationField()
    class DestinationWarehouse : ValidationField()
    class CurrentWarehouse : ValidationField()
    class Entity : ValidationField()
}

fun ValidationField.displayName(): String = when (this) {
    is ValidationField.Id -> "ID"
    is ValidationField.Name -> "Name"
    is ValidationField.RegionalZone -> "Regional zone"
    is ValidationField.Latitude -> "Latitude"
    is ValidationField.Longitude -> "Longitude"
    is ValidationField.Weight -> "Weight"
    is ValidationField.Priority -> "Priority"
    is ValidationField.DistanceKm -> "Distance"
    is ValidationField.TypicalDelayMin -> "Typical delay"
    is ValidationField.MaxCapacityKg -> "Maximum capacity"
    is ValidationField.CostPerKm -> "Cost per kilometer"
    is ValidationField.OriginWarehouse -> "Origin warehouse"
    is ValidationField.DestinationWarehouse -> "Destination warehouse"
    is ValidationField.CurrentWarehouse -> "Current warehouse"
    is ValidationField.Entity -> "Entity"
}