package org.bytebloom.logiroute.data.csv.exception

class CsvFileNotFoundException(fileName: String, directory: String) :
    DataException("CSV file '$fileName' was not found in '$directory'.")