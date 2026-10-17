package org.bytebloom.logiroute.domain.commandPattern

interface Command {
    fun execute():Boolean
    fun undo():Boolean
}
