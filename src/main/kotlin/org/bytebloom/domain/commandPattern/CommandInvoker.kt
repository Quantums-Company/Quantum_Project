package org.bytebloom.domain.commandPattern

class CommandInvoker {

    private val history = ArrayDeque<Command>()

    fun execute(command: Command): Boolean {
        command.execute()
        history.addLast(command)
        return true
    }

    fun undo(): Boolean {
        val command = history.lastOrNull()
            ?: return false

        command.undo()
        history.removeLast()

        return true
    }

    fun canUndo(): Boolean =
        history.isNotEmpty()

    fun clearHistory() {
        history.clear()
    }
}
