package org.bytebloom.domain.commandPattern

class CommandInvoker {
    private val history = ArrayDeque<Command>()
    fun execute(command: Command) {
        command.execute()
        history.addLast(command)
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

