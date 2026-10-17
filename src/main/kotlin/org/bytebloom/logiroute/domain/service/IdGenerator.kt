package org.bytebloom.logiroute.domain.service

import org.bytebloom.logiroute.domain.model.EntityType

interface IdGenerator {
    fun next(entityType: EntityType): String
}