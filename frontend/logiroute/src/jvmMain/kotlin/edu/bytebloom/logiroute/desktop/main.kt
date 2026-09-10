package edu.bytebloom.logiroute.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import edu.logiroute.logiroute.App
import quantum_project.frontend.logiroute.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import quantum_project.frontend.logiroute.generated.resources.byteloom_logo

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "ByteBloom LogiRoute",
        resizable = true,
        icon = painterResource(Res.drawable.byteloom_logo)
    ) {
        App()
    }
}