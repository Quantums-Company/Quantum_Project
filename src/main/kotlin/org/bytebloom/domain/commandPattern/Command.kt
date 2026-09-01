package org.bytebloom.domain.commandPattern

interface Command {
    fun execute()
    fun undo()
}
