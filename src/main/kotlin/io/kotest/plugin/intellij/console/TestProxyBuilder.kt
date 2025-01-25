package io.kotest.plugin.intellij.console

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import io.kotest.plugin.intellij.KotestTestLocator

data class TestProxyBuilder(val name: String, val suite: Boolean, val parent: SMTestProxy) {
   companion object {
      fun builder(name: String, suite: Boolean, parent: SMTestProxy): TestProxyBuilder {
         return TestProxyBuilder(name, suite, parent)
      }
   }

   fun build(): SMTestProxy {
      val proxy = SMTestProxy(name, suite, null)
      parent.addChild(proxy)
      proxy.locator = KotestTestLocator()
      return proxy
   }
}
