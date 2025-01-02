package com.sksamuel.kotest.engine.spec.isolation.perleaf

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val tests = mutableSetOf<String>()
private val specs = mutableSetOf<Int>()

private var buffer = ""

class FreeSpecPerLeafTest : FreeSpec({

   afterProject {
      tests.size shouldBe 9
      specs.size shouldBe 5
   }

   afterSpec {
      specs.add(it.hashCode())
   }

   afterTest {
      tests.add(it.a.name.name)
   }

   afterProject {
      buffer shouldBe "abcabdaefghgi"
   }

   isolationMode = IsolationMode.InstancePerLeaf

   val count = AtomicInteger(0)

   "a" - {
      count.incrementAndGet().shouldBe(1)
      buffer += "a"
      "b" - {
         count.incrementAndGet().shouldBe(2)
         buffer += "b"
         "c" {
            count.incrementAndGet().shouldBe(3)
            buffer += "c"
         }
         "d" {
            count.incrementAndGet().shouldBe(3)
            buffer += "d"
         }
      }
      "e" - {
         count.incrementAndGet().shouldBe(2)
         buffer += "e"
         "f" {
            count.incrementAndGet().shouldBe(3)
            buffer += "f"
         }
      }
   }
   "g" - {
      count.incrementAndGet().shouldBe(1)
      buffer += "g"
      "h" {
         count.incrementAndGet().shouldBe(2)
         buffer += "h"
      }
      "i" {
         count.incrementAndGet().shouldBe(2)
         buffer += "i"
      }
   }

})
