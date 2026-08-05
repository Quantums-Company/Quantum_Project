package org.bytebloom.util

object Logger {

    fun warning(message: String) {
        println("Warning: $message")
    }

    fun error(message: String) {
        println("Error: $message")
    }

    fun info(message: String) {
        println(message)
    }
}