package org.bytebloom.domain.commandPattern

import org.bytebloom.util.Logger

class CommandInvoker {

    private val undoStack = ArrayDeque<Command>()
    private val redoStack = ArrayDeque<Command>()

    fun execute(command: Command): Boolean {
        if (!command.execute()) {
            return false
        }

        undoStack.addLast(command)
        redoStack.clear()

        Logger.info(
            "Command executed successfully. " +
                    "Undo available: ${undoStack.size}, " +
                    "Redo available: ${redoStack.size}"
        )

        return true
    }

    fun undo(): Boolean {
        val command = undoStack.removeLastOrNull()
            ?: return false

        if (!command.undo()) {
            undoStack.addLast(command)
            return false
        }

        redoStack.addLast(command)

        Logger.info(
            "Command undone successfully. " +
                    "Undo available: ${undoStack.size}, " +
                    "Redo available: ${redoStack.size}"
        )

        return true
    }

    fun redo(): Boolean {
        val command = redoStack.removeLastOrNull()
            ?: return false

        if (!command.execute()) {
            redoStack.addLast(command)
            return false
        }

        undoStack.addLast(command)

        Logger.info(
            "Command redone successfully. " +
                    "Undo available: ${undoStack.size}, " +
                    "Redo available: ${redoStack.size}"
        )

        return true
    }

    fun canUndo(): Boolean =
        undoStack.isNotEmpty()

    fun canRedo(): Boolean =
        redoStack.isNotEmpty()

    fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
    }
}
