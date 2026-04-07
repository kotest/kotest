package io.kotest.android

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.Rule
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
@RunWith(KotestTestRunner::class)
open class ComposeWithKotest : FreeSpec() {

   @get:Rule
   val composeTestRule: ComposeContentTestRule = createComposeRule()

   init {

      // ComposeContentTestRule holds a TestCoroutineScheduler that cannot be reused across tests.
      // InstancePerTest creates a fresh spec instance (and thus a fresh composeTestRule) per root.
      isolationMode = IsolationMode.InstancePerRoot

      "should have initial state of 0" {

         composeTestRule.setContent {
            TestComposable()
         }

         composeTestRule.onNodeWithText("0").assertExists()
         composeTestRule.onNodeWithText("Click me!").assertExists()
      }
      "should increment when clicked" {

         composeTestRule.setContent {
            TestComposable()
         }

         composeTestRule.onNodeWithText("0").assertExists()
         composeTestRule.onNodeWithText("Click me!").assertExists()
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
