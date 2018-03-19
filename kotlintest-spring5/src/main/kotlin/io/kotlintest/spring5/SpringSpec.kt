package io.kotlintest.spring5

import io.kotlintest.core.Spec
import io.kotlintest.core.TestCaseContext
import org.springframework.test.context.TestContextManager

class SpringAutowired : Spec {

  val manager = TestContextManager(javaClass)

  override fun interceptSpec(spec: () -> Unit) {
    manager.beforeTestClass()
    super.interceptSpec(spec)
    manager.afterTestClass()
  }

  override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
    manager.beforeTestExecution(this, null)
    manager.beforeTestMethod(this, null)
    super.interceptTestCase(context, test)
    manager.afterTestMethod(this, null, null)
    manager.afterTestExecution(this, null, null)
  }
}