package io.kotest.plugin.intellij.console

import com.intellij.execution.testframework.sm.runner.SMTestProxy

object TestProxyUpdater {

   fun setFailed(proxy: SMTestProxy, attrs: MessageAttributes) {
      // the test error flag determines if intellij shows a red test icon or a yellow warning icon
      // yellow is typically used for assertion failures and red for build or general errors
      val testError = attrs.resultStatus == "Error"
      proxy.setTestFailed(attrs.message, attrs.details, testError)
   }
}
