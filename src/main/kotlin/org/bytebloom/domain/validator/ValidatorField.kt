package org.bytebloom.domain.validator

sealed class ValidatorField {
    class Id : ValidatorField()
    class Name : ValidatorField()
    class RegionalZone : ValidatorField()
    class Latitude : ValidatorField()
    class Longitude : ValidatorField()
    class Weight : ValidatorField()
    class Priority : ValidatorField()
    class DistanceKm : ValidatorField()
    class TypicalDelayMin : ValidatorField()
    class MaxCapacityKg : ValidatorField()
    class CostPerKm : ValidatorField()
    class OriginWarehouse : ValidatorField()
    class DestinationWarehouse : ValidatorField()
    class CurrentWarehouse : ValidatorField()
    class Entity : ValidatorField()
}

fun ValidatorField.displayName(): String = when (this) {
    is ValidatorField.Id -> "ID"
    is ValidatorField.Name -> "Name"
    is ValidatorField.RegionalZone -> "Regional zone"
    is ValidatorField.Latitude -> "Latitude"
    is ValidatorField.Longitude -> "Longitude"
    is ValidatorField.Weight -> "Weight"
    is ValidatorField.Priority -> "Priority"
    is ValidatorField.DistanceKm -> "Distance"
    is ValidatorField.TypicalDelayMin -> "Typical delay"
    is ValidatorField.MaxCapacityKg -> "Maximum capacity"
    is ValidatorField.CostPerKm -> "Cost per kilometer"
    is ValidatorField.OriginWarehouse -> "Origin warehouse"
    is ValidatorField.DestinationWarehouse -> "Destination warehouse"
    is ValidatorField.CurrentWarehouse -> "Current warehouse"
    is ValidatorField.Entity -> "Entity"
}