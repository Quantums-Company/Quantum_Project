package org.bytebloom.logiroute.ui.terminal

private const val ANSI_RESET_CODE = "0"
private const val ANSI_BOLD_CODE = "1"
private const val ANSI_DIM_CODE = "2"
private const val ANSI_RED_CODE = "31"
private const val ANSI_GREEN_CODE = "32"
private const val ANSI_YELLOW_CODE = "33"
private const val ANSI_BLUE_CODE = "34"
private const val ANSI_MAGENTA_CODE = "35"
private const val ANSI_CYAN_CODE = "36"

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

    data class BoxChars(
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
        val cross: String
    ) {
        companion object {
            val UNICODE = BoxChars(
                "─",
                "│",
                "╭",
                "╮",
                "╰",
                "╯",
                "┬",
                "┴",
                "├",
                "┤",
                "┼"
            )
            val ASCII = BoxChars(
                "-",
                "|",
                "+",
                "+",
                "+",
                "+",
                "+",
                "+",
                "+",
                "+",
                "+"
            )
        }
    }

    data class UiSymbols(
        val arrow: String,
        val barFilled: String,
        val barEmpty: String,
        val warning: String,
        val success: String,
        val bullet: String,
        val spinner: List<String>
    ) {
        companion object {
            val UNICODE = UiSymbols(
                "→",
                "█",
                "░",
                "⚠",
                "✔",
                "•",
                listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏")
            )
            val ASCII = UiSymbols(
                "->",
                "#",
                ".",
                "!",
                "OK",
                "*",
                listOf("|", "/", "-", "\\")
            )
        }
    }

    class Glyphs(
        val box: BoxChars,
        val symbols: UiSymbols
    ) {
        val horizontal: String get() = box.horizontal
        val vertical: String get() = box.vertical
        val topLeft: String get() = box.topLeft
        val topRight: String get() = box.topRight
        val bottomLeft: String get() = box.bottomLeft
        val bottomRight: String get() = box.bottomRight
        val teeDown: String get() = box.teeDown
        val teeUp: String get() = box.teeUp
        val teeRight: String get() = box.teeRight
        val teeLeft: String get() = box.teeLeft
        val cross: String get() = box.cross
        val arrow: String get() = symbols.arrow
        val barFilled: String get() = symbols.barFilled
        val barEmpty: String get() = symbols.barEmpty
        val warning: String get() = symbols.warning
        val success: String get() = symbols.success
        val spinner: List<String> get() = symbols.spinner

        companion object {
            val UNICODE = Glyphs(BoxChars.UNICODE, UiSymbols.UNICODE)
            val ASCII = Glyphs(BoxChars.ASCII, UiSymbols.ASCII)
        }
    }

    companion object {
        private const val ESC = "\u001B"
        private const val RESET = ANSI_RESET_CODE
        private const val BOLD = ANSI_BOLD_CODE
        private const val DIM = ANSI_DIM_CODE
        private const val RED = ANSI_RED_CODE
        private const val GREEN = ANSI_GREEN_CODE
        private const val YELLOW = ANSI_YELLOW_CODE
        private const val BLUE = ANSI_BLUE_CODE
        private const val MAGENTA = ANSI_MAGENTA_CODE
        private const val CYAN = ANSI_CYAN_CODE

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