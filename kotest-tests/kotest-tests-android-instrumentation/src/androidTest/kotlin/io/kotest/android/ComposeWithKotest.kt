package io.kotest.android

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

/**
 * Instrumented test using Kotest, which will execute on an Android device.
 *
 * Instrumented tests run on an Android device, either physical or emulated.
 * The app is built and installed alongside a test app that injects commands and reads the state.
 * Instrumented tests are usually UI tests, launching an app and then interacting with it.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(KotestTestRunner::class)
class ComposeWithKotest : FreeSpec() {
   init {
      "should have initial state of 0" {
         runComposeUiTest {
            setContent {
               TestComposable()
            }
            onNodeWithText("0").assertExists()
            onNodeWithText("Click me!").assertExists()
         }
      }
      "should increment when clicked" {
         runComposeUiTest {
            setContent {
               TestComposable()
            }
            onNodeWithText("0").assertExists()
            onNodeWithText("Click me!").assertExists()
            onNodeWithText("Click me!").performClick()
            onNodeWithText("1").assertExists()
         }
      }
   }
}

/**
 * Simple composable that has a counter and a button that will increase the counter
 * each time it is clicked.
 */
@Composable
private fun TestComposable() {
   var counter by remember { mutableStateOf(0) }
   Button(onClick = { counter++ }) { Text("Click me!") }
   Text(counter.toString())
}
