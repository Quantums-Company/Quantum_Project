package org.bytebloom.domain.service

import org.bytebloom.domain.model.EntityType

interface IdGenerator {
    fun next(entityType: EntityType): String
}