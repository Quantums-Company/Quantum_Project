package org.bytebloom.logiroute.data.csv.exception

sealed class DataException(message: String) : Exception(message){
    companion object{
        const val MSG_INVALID_CHARACTER = "Invalid Geohash character"
    }
}

class InvalidCharacterException(char: Char) : DataException("$MSG_INVALID_CHARACTER: $char")