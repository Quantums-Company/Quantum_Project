package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Warehouse

/** What the package management screen must be able to show. Implemented by the UI. */
interface PackagesManagementScreen : BaseScreen {

    fun showPackages(packages: List<Package>)

    /** The warehouses a package can travel between, to offer them in a picker. */
    fun showWarehouseChoices(warehouses: List<Warehouse>)

    fun showPackage(pkg: Package)

    fun showPackageNotFound(id: String)

    fun showPackageCreated(pkg: Package)

    fun showPackageUpdated(pkg: Package)

    fun showPackageDeleted(id: String, wasDeleted: Boolean)

    /** The result of a search. [criteria] is a readable sentence such as "heavier than 20.0 kg". */
    fun showMatchingPackages(criteria: String, packages: List<Package>)
}
