package org.bytebloom.logiroute.ui.terminal.widget

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.ui.terminal.ConsoleStyle

private const val INDEX_HEADER = "Index"
private const val INDEX_COLUMN = 0
private const val COORDINATE_DECIMALS = 4
private const val PICK_HINT = " - type an index or an id to pick any of them"

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
    val idColumn = if (numbered) 1 else 0
    val base = mutableMapOf<Int, (String) -> String>(idColumn to { cell -> bold(cyan(cell)) })
    if (numbered) base[INDEX_COLUMN] = { cell -> yellow(cell) }
    extra.forEach { (column, painter) -> base[if (numbered) column + 1 else column] = painter }
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
        rightAligned = shifted(setOf(3, 4), numbered),
        cellColors = colors(numbered, mapOf(2 to { cell -> magenta(cell) })),
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
            listOf(it.id, formatNumber(
                it.maxCapacityKg,
                1
            ), formatNumber(it.costPerKm), it.currentWarehouse.name)
        },
        rightAligned = shifted(setOf(1, 2), numbered),
        cellColors = colors(numbered, mapOf(3 to { cell -> magenta(cell) })),
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
            listOf(it.id, formatNumber(
                it.weight,
                1
            ), it.priority.name, it.originWarehouse.name, it.destinationWarehouse.name)
        },
        rightAligned = shifted(setOf(1), numbered),
        cellColors = colors(numbered, mapOf(2 to { cell -> priorityColor(cell) })),
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
        rightAligned = shifted(setOf(3, 4), numbered),
        cellColors = colors(numbered),
        limit = limit,
        moreHint = hint(numbered)
    )

