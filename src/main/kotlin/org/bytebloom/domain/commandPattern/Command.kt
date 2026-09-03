package org.bytebloom.domain.commandPattern

interface Command {
    fun execute():Boolean
    fun undo():Boolean
}
