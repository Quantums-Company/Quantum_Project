package org.bytebloom.logiroute.ui.terminal

/** One line of the menu. [aliases] are the words (besides its number) that open it. */
class MenuEntry(
    val title: String,
    val aliases: Set<String>,
    val screen: TerminalScreen
)