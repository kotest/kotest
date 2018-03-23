package io.kotlintest.spring5

import io.kotlintest.Spec
import io.kotlintest.extensions.SpecExtension
import org.springframework.test.context.TestContextManager

object SpringSpecExtension : SpecExtension {
  override fun intercept(spec: Spec, process: () -> Unit) {
    val manager = TestContextManager(spec.javaClass)
    val ac = manager.testContext.applicationContext
    ac.autowireCapableBeanFactory.autowireBean(spec)
    process()
  }
}