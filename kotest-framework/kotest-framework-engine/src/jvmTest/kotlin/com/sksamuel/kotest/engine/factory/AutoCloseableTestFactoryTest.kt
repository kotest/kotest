package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.shouldSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class AutoCloseableTestFactoryTest : ShouldSpec({
   include(
      shouldSpec {
         include(
            shouldSpec {
               should("close is called in nested test factory") {
                  autoClose(FirstAutoClose)
               }
            }
         )
         should("close is called in test factory") {
            autoClose(SecondAutoClose)
         }
      }
   )
   should("close is called") {
      autoClose(ThirdAutoClose)
   }
   afterSpec {
      FirstAutoClose.closed shouldBe 3
      SecondAutoClose.closed shouldBe 2
      ThirdAutoClose.closed shouldBe 1
   }
})

private object FirstAutoClose : AutoCloseable {
   var closed = -1

   override fun close() {
      closed = closedNumber.incrementAndGet()
   }
}

private object SecondAutoClose : AutoCloseable {
   var closed = -1

   override fun close() {
      closed = closedNumber.incrementAndGet()
   }
}

private object ThirdAutoClose : AutoCloseable {
   var closed = -1

   override fun close() {
      closed = closedNumber.incrementAndGet()
   }
}

private var closedNumber = AtomicInteger(0)
