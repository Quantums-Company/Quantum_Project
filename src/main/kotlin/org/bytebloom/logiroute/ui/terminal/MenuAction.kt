package org.bytebloom.logiroute.ui.terminal

/** One choice of a screen's own sub-menu, for example "Create" or "Delete". */
class MenuAction(
    val label: String,
    val aliases: Set<String>,
    val run: suspend () -> Unit
)