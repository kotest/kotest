package io.kotlintest.spring

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.extensions.TestListener
import org.springframework.test.context.TestContextManager

object SpringListener : TestListener {

  override fun specStarted(description: Description, spec: Spec) {
    try {
      val manager = TestContextManager(spec.javaClass)
      val ac = manager.testContext.applicationContext
      ac.autowireCapableBeanFactory.autowireBean(spec)
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }
}