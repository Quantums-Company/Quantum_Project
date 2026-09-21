package org.bytebloom.data.local.common

import org.bytebloom.domain.service.IdGenerator
import java.util.UUID

class UuidIdGenerator : IdGenerator {
    override fun next(prefix: String): String = "$prefix-${UUID.randomUUID()}"
}