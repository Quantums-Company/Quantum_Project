package org.bytebloom.domain.commandPattern

class CommandInvoker {

    private val history = ArrayDeque<Command>()

    fun execute(command: Command): Boolean {
        if(command.execute()){
            history.addLast(command)
            return true
        }

        return false
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
