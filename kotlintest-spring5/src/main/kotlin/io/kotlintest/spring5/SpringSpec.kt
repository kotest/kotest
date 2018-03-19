package io.kotlintest.spring5

import io.kotlintest.Spec
import io.kotlintest.TestCaseContext

interface SpringAutowired : Spec {

  override fun interceptSpec(spec: () -> Unit) {
    TestContextManagerHolder.manager.beforeTestClass()
    super.interceptSpec(spec)
    TestContextManagerHolder.manager.afterTestClass()
  }

  override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
    TestContextManagerHolder.manager.beforeTestExecution(this, null)
    TestContextManagerHolder.manager.beforeTestMethod(this, null)
    super.interceptTestCase(context, test)
    TestContextManagerHolder.manager.afterTestMethod(this, null, null)
    TestContextManagerHolder.manager.afterTestExecution(this, null, null)
  }
}