@file:Suppress("unused")

package io.kotest.engine.launcher

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.MultipleExceptions

actual suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?) {

   val result = TestEngineLauncher()
      .withSpecRefs(specs)
      .withProjectConfig(config)
      // we don't need this from inside intellij as the test output will be present in the tree view anyway,
      // but we don't have a way of detecting intellij from kotlin native so aren't able to detect when to skip
      .withConsoleListener()
      // TCSM is always included to hook into the native test task reporting
      // also, the Gradle test task will capture stdout when it receives a TCSM test-started event until it receives
      // a test-finished event, so this TCSM listener must come after the console listener, otherwise, some console
      // output will be swallowed
      //
      // TODO under TestExecutionMode.Concurrent/SpecExecutionMode.Concurrent, withTeamCityListener()'s
      // PinnedTestEngineListener (see kotest#6188) can hold one test's TC node open for its whole real
      // duration while a faster sibling finishes underneath it. Console output printed by that faster
      // sibling in the meantime still gets attributed to whichever TC node is currently open (this test),
      // per the "swallowed" comment above -- so a fast test's own console progress line can visibly show
      // up nested under a slower, still-open sibling's node in Gradle/IntelliJ's output pane. Results
      // (pass/fail/timing) are still correct; this is a console-output-attribution artifact only.
      .withTeamCityListener()
      .execute()

   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }
}
