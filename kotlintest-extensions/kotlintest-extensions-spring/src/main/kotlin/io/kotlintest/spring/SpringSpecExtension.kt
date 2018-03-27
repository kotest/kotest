package io.kotlintest.spring

import io.kotlintest.Spec
import io.kotlintest.extensions.SpecInterceptor
import org.springframework.test.context.TestContextManager

object SpringSpecExtension : SpecInterceptor {
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