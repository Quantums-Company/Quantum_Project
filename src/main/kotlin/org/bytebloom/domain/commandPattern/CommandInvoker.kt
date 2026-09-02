package org.bytebloom.domain.commandPattern

class CommandInvoker {
    private val history = ArrayDeque<Command>()
    fun execute(command: Command):Boolean {
        return try {
            command.execute()
            history.addLast(command)
            true
        }
        catch (e: Exception){
            throw e
        }
    }
    fun undo(): Boolean {
        if (history.isEmpty()) {
            return false
        }
        val command = history.removeLast()
        command.undo()
        return true
    }
    fun canUndo(): Boolean =
        history.isNotEmpty()

    fun clearHistory() {
        history.clear()
    }

    }
