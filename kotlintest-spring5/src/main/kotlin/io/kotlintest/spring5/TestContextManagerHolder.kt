package io.kotlintest.spring5

import org.springframework.test.context.TestContextManager

object TestContextManagerHolder {
  val manager: TestContextManager by lazy {
    TestContextManager(javaClass)
  }
}