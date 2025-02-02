package io.kotest.plugin.intellij.console

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import io.kotest.plugin.intellij.locations.KotestTestLocator

data class TestProxyBuilder(val name: String, val suite: Boolean, val location: String?, val parent: SMTestProxy) {

   companion object {
      fun builder(name: String, suite: Boolean, location: String?, parent: SMTestProxy): TestProxyBuilder {
         return TestProxyBuilder(name, suite, location, parent)
      }
   }

   fun build(): SMTestProxy {
      val proxy = SMTestProxy(name, suite, location)
      parent.addChild(proxy)
      proxy.locator = KotestTestLocator()
      return proxy
   }
}
