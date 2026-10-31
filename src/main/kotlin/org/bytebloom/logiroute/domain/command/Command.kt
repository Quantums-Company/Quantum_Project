package org.bytebloom.logiroute.domain.command

interface Command {
    fun execute(): Boolean
    fun undo(): Boolean
}