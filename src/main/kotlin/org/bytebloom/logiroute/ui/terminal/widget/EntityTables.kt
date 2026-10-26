package org.bytebloom.logiroute.ui.terminal.widget

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle

private const val INDEX_HEADER = "Index"
private const val INDEX_COLUMN = 0
private const val CAPACITY_DECIMALS = 1
private const val COORDINATE_DECIMALS = 4
private const val PICK_HINT = " - type an index or an id to pick any of them"

private const val COL_ONE = 1
private const val COL_TWO = 2
private const val COL_THREE = 3
private const val COL_FOUR = 4

private fun <T> numberedRows(
    items: List<T>,
    numbered: Boolean,
    cells: (T) -> List<String>
): List<List<String>> =
    items.mapIndexed { index, item ->
        if (numbered) listOf((index + 1).toString()) + cells(item) else cells(item)
    }

private fun withIndex(headers: List<String>, numbered: Boolean) =
    if (numbered) listOf(INDEX_HEADER) + headers else headers

private fun shifted(columns: Set<Int>, numbered: Boolean): Set<Int> =
    if (numbered) columns.map { it + 1 }.toSet() + INDEX_COLUMN else columns

private fun ConsoleStyle.colors(
    numbered: Boolean,
    extra: Map<Int, (String) -> String> = emptyMap()
): Map<Int, (String) -> String> {
    val idColumn = if (numbered) COL_ONE else INDEX_COLUMN
    val base = mutableMapOf<Int, (String) -> String>(idColumn to { cell -> bold(cyan(cell)) })
    if (numbered) base[INDEX_COLUMN] = { cell -> yellow(cell) }
    extra.forEach { (column, painter) -> base[if (numbered) column + COL_ONE else column] = painter }
    return base
}

private fun ConsoleStyle.priorityColor(text: String): String = when (text) {
    Priority.URGENT.name -> red(text)
    Priority.STANDARD.name -> yellow(text)
    else -> dim(text)
}

private fun hint(numbered: Boolean) = if (numbered) PICK_HINT else ""

fun ConsoleStyle.warehouseTable(
    warehouses: List<Warehouse>,
    numbered: Boolean = false,
    limit: Int? = DEFAULT_ROW_LIMIT
): List<String> =
    table(
        headers = withIndex(listOf("ID", "Name", "Zone", "Latitude", "Longitude"), numbered),
        rows = numberedRows(warehouses, numbered) {
            listOf(
                it.id,
                it.name,
                it.regionalZone,
                formatNumber(it.latitude, COORDINATE_DECIMALS),
                formatNumber(it.longitude, COORDINATE_DECIMALS)
            )
        },
        rightAligned = shifted(setOf(COL_THREE, COL_FOUR), numbered),
        cellColors = colors(numbered, mapOf(COL_TWO to { cell -> magenta(cell) })),
        limit = limit,
        moreHint = hint(numbered)
    )

fun ConsoleStyle.vehicleTable(
    vehicles: List<Vehicle>,
    numbered: Boolean = false,
    limit: Int? = DEFAULT_ROW_LIMIT
): List<String> =
    table(
        headers = withIndex(listOf("ID", "Capacity (kg)", "Cost / km", "Stationed at"), numbered),
        rows = numberedRows(vehicles, numbered) {
            listOf(
                it.id,
                formatNumber(it.maxCapacityKg, CAPACITY_DECIMALS),
                formatNumber(it.costPerKm),
                it.currentWarehouse.name
            )
        },
        rightAligned = shifted(setOf(COL_ONE, COL_TWO), numbered),
        cellColors = colors(numbered, mapOf(COL_THREE to { cell -> magenta(cell) })),
        limit = limit,
        moreHint = hint(numbered)
    )

fun ConsoleStyle.packageTable(
    packages: List<Package>,
    numbered: Boolean = false,
    limit: Int? = DEFAULT_ROW_LIMIT
): List<String> =
    table(
        headers = withIndex(listOf("ID", "Weight (kg)", "Priority", "From", "To"), numbered),
        rows = numberedRows(packages, numbered) {
            listOf(
                it.id,
                formatNumber(it.weight, CAPACITY_DECIMALS),
                it.priority.name,
                it.originWarehouse.name,
                it.destinationWarehouse.name
            )
        },
        rightAligned = shifted(setOf(COL_ONE), numbered),
        cellColors = colors(numbered, mapOf(COL_TWO to { cell -> priorityColor(cell) })),
        limit = limit,
        moreHint = hint(numbered)
    )

fun ConsoleStyle.routeTable(
    routes: List<Route>,
    numbered: Boolean = false,
    limit: Int? = DEFAULT_ROW_LIMIT
): List<String> =
    table(
        headers = withIndex(listOf("ID", "From", "To", "Distance (km)", "Delay (min)"), numbered),
        rows = numberedRows(routes, numbered) {
            listOf(
                it.id,
                it.originWarehouse.name,
                it.destinationWarehouse.name,
                formatNumber(it.distanceKm),
                it.typicalDelayMin.toString()
            )
        },
        rightAligned = shifted(setOf(COL_THREE, COL_FOUR), numbered),
        cellColors = colors(numbered),
        limit = limit,
        moreHint = hint(numbered)
    )

