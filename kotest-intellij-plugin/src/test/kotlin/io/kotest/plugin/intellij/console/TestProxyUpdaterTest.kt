package io.kotest.plugin.intellij.console

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo
import io.kotest.matchers.shouldBe
import org.junit.Test

@Suppress("UnstableApiUsage")
class TestProxyUpdaterTest {

   private val root = SMTestProxy("root", true, null)

   @Test
   fun testError() {
      val proxy = TestProxyBuilder.builder(name = "name", suite = true, parent = root).build()
      TestProxyUpdater.setFailed(
         proxy,
         MessageAttributes(
            id = "id",
            parentId = "parent",
            name = "name",
            location = null,
            duration = null,
            message = "foo",
            details = null,
            resultStatus = "Error"
         )
      )
      proxy.isDefect shouldBe true
      proxy.errorMessage shouldBe "foo"
      proxy.magnitude shouldBe TestStateInfo.Magnitude.ERROR_INDEX.value
   }

   @Test
   fun testFailure() {
      val proxy = TestProxyBuilder.builder(name = "name", suite = true, parent = root).build()
      TestProxyUpdater.setFailed(
         proxy,
         MessageAttributes(
            id = "id",
            parentId = "parent",
            name = "name",
            location = null,
            duration = null,
            message = "Test failed",
            details = null,
            resultStatus = "Failure"
         )
      )
      proxy.isDefect shouldBe true
      proxy.errorMessage shouldBe "Test failed"
      proxy.magnitude shouldBe TestStateInfo.Magnitude.FAILED_INDEX.value
   }
}
