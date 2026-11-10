package edu.bytebloom.logiroute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _root_ide_package_.edu.logiroute.App()
        }
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    _root_ide_package_.edu.logiroute.App()
}