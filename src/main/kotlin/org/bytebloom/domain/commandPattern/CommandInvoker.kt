package org.bytebloom.domain.commandPattern

class CommandInvoker {

    private val undoStack = ArrayDeque<Command>()
    private val redoStack = ArrayDeque<Command>()

    fun execute(command: Command): Boolean {
        if (!command.execute()) return false
        undoStack.addLast(command)
        redoStack.clear()
        return true
    }

    fun undo(): Boolean {
        val command = undoStack.removeLastOrNull() ?: return false
        if (!command.undo()) {
            undoStack.addLast(command)
            return false
        }
        redoStack.addLast(command)
        return true
    }

    fun redo(): Boolean {
        val command = redoStack.removeLastOrNull() ?: return false
        if (!command.execute()) {
            redoStack.addLast(command)
            return false
        }
        undoStack.addLast(command)
        return true
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()
    fun undoAvailable(): Int = undoStack.size
    fun redoAvailable(): Int = redoStack.size

    fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
    }
}
