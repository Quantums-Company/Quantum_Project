package org.bytebloom.logiroute.data.csv.io

enum class CsvTable(val fileName: String, val columnCount: Int) {
    PACKAGES(fileName = "packages.csv", columnCount = 5),
    ROUTES(fileName = "routes.csv", columnCount = 5),
    VEHICLES(fileName = "fleet.csv", columnCount = 4),
    WAREHOUSES(fileName = "warehouses.csv", columnCount = 5)
}