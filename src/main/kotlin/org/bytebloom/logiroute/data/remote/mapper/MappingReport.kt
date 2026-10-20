package org.bytebloom.logiroute.data.remote.mapper

data class MappingReport<Domain>(
    val succeeded: List<Domain>,
    val skipped: List<String>
)