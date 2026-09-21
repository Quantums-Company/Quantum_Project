package org.bytebloom.domain.service

interface IdGenerator {
    fun next(prefix: String): String
}