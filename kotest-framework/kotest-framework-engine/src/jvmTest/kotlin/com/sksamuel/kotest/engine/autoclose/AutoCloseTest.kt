package com.sksamuel.kotest.engine.autoclose

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class AutoCloseTest : StringSpec() {

  private val first = autoClose(FirstAutoclose)
  private val second = autoClose(SecondAutoclose)
  private val third = autoClose(ThirdAutoclose)

  init {
    "should close resources in reverse order" {
       // Test will be verified in AfterSpec
    }
  }

   override fun afterSpec(spec: Spec) {
      FirstAutoclose.closed shouldBe 3
      SecondAutoclose.closed shouldBe 2
      ThirdAutoclose.closed shouldBe 1
   }

}

private val closedNumber = AtomicInteger(0)

private object FirstAutoclose : AutoCloseable {

   var closed = -1

   override fun close() {
      closed = closedNumber.incrementAndGet()
   }
}

private object SecondAutoclose : AutoCloseable {
   var closed = -1

   override fun close() {
      closed = closedNumber.incrementAndGet()
   }
}

private object ThirdAutoclose : AutoCloseable {
   var closed = -1

   override fun close() {
      closed = closedNumber.incrementAndGet()
   }
}
