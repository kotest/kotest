package io.kotlintest.spring

import io.kotlintest.Spec
import io.kotlintest.extensions.SpecExtension
import org.springframework.test.context.TestContextManager

object SpringSpecExtension : SpecExtension {
  override fun intercept(spec: Spec, process: () -> Unit) {
    try {
      val manager = TestContextManager(spec.javaClass)
      val ac = manager.testContext.applicationContext
      ac.autowireCapableBeanFactory.autowireBean(spec)
      process()
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }
}