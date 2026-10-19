package org.bytebloom.logiroute.ui.terminal

/**
 * Colors and drawing characters, decided once.
 * [colors] = ANSI colors on/off, [unicode] = box-drawing characters or plain ASCII.
 */
class ConsoleStyle(
    val colors: Boolean,
    val unicode: Boolean
) {
    val glyphs: Glyphs = if (unicode) Glyphs.UNICODE else Glyphs.ASCII

    fun bold(text: String) = paint(text, BOLD)
    fun dim(text: String) = paint(text, DIM)
    fun red(text: String) = paint(text, RED)
    fun green(text: String) = paint(text, GREEN)
    fun yellow(text: String) = paint(text, YELLOW)
    fun blue(text: String) = paint(text, BLUE)
    fun magenta(text: String) = paint(text, MAGENTA)
    fun cyan(text: String) = paint(text, CYAN)

    private fun paint(text: String, code: String): String =
        if (colors) "$ESC[${code}m$text$ESC[${RESET}m" else text

    class Glyphs @Suppress("LongParameterList") constructor(
        val horizontal: String,
        val vertical: String,
        val topLeft: String,
        val topRight: String,
        val bottomLeft: String,
        val bottomRight: String,
        val teeDown: String,
        val teeUp: String,
        val teeRight: String,
        val teeLeft: String,
        val cross: String,
        val arrow: String,
        val barFilled: String,
        val barEmpty: String,
        val warning: String,
        val success: String,
        val bullet: String,
        val spinner: List<String>
    ) {
        companion object {
            val UNICODE = Glyphs(
                horizontal = "─", vertical = "│",
                topLeft = "╭", topRight = "╮", bottomLeft = "╰", bottomRight = "╯",
                teeDown = "┬", teeUp = "┴", teeRight = "├", teeLeft = "┤", cross = "┼",
                arrow = "→", barFilled = "█", barEmpty = "░",
                warning = "⚠", success = "✔", bullet = "•",
                spinner = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏")
            )
            val ASCII = Glyphs(
                horizontal = "-", vertical = "|",
                topLeft = "+", topRight = "+", bottomLeft = "+", bottomRight = "+",
                teeDown = "+", teeUp = "+", teeRight = "+", teeLeft = "+", cross = "+",
                arrow = "->", barFilled = "#", barEmpty = ".",
                warning = "!", success = "OK", bullet = "*",
                spinner = listOf("|", "/", "-", "\\")
            )
        }
    }

    companion object {
        private const val ESC = "\u001B"
        private const val RESET = "0"
        private const val BOLD = "1"
        private const val DIM = "2"
        private const val RED = "31"
        private const val GREEN = "32"
        private const val YELLOW = "33"
        private const val BLUE = "34"
        private const val MAGENTA = "35"
        private const val CYAN = "36"

        private val ANSI_SEQUENCE = Regex("\u001B\\[[0-9;]*[A-Za-z]")

        /** Length of [text] as the user sees it: color codes take no room on screen. */
        fun visibleLength(text: String): Int = text.replace(ANSI_SEQUENCE, "").length

        /** Colors unless NO_COLOR is set; ASCII only when LOGIROUTE_ASCII is set. */
        fun detect(environment: Map<String, String> = System.getenv()): ConsoleStyle = ConsoleStyle(
            colors = environment["NO_COLOR"] == null && environment["TERM"] != "dumb",
            unicode = environment["LOGIROUTE_ASCII"] == null
        )
    }
}
