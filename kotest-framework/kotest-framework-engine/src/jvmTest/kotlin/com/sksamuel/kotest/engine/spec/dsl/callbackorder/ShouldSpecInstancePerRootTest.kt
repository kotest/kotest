package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

object Counters2 {
   var specs = mutableSetOf<Int>()
   var executed = mutableListOf<String>()
   var threads = mutableSetOf<Long>()
}

class ShouldSpecInstancePerRootTest : ShouldSpec() {

   override fun isolationMode() = IsolationMode.InstancePerRoot

   override suspend fun beforeTest(testCase: TestCase) {
      Counters2.specs.add(testCase.spec.hashCode())
   }

   init {

      afterProject {
         Counters2.specs.size shouldBe 3
         Counters2.threads.size shouldBe 1
         withClue("riker") {
            Counters2.executed.count { it == "riker" } shouldBe 3
         }
         withClue("data") {
            Counters2.executed.count { it == "data" } shouldBe 1
         }
         withClue("lwaxana") {
            Counters2.executed.count { it == "lwaxana" } shouldBe 1
         }
         withClue("crusher") {
            Counters2.executed.count { it == "crusher" } shouldBe 4
         }
         withClue("worf") {
            Counters2.executed.count { it == "worf" } shouldBe 1
         }
         withClue("keiko") {
            Counters2.executed.count { it == "keiko" } shouldBe 1
         }
         withClue("mott") {
            Counters2.executed.count { it == "mott" } shouldBe 4
         }
         withClue("ro") {
            Counters2.executed.count { it == "ro" } shouldBe 1
         }
         withClue("obrien") {
            Counters2.executed.count { it == "obrien" } shouldBe 2
         }
         withClue("barclay") {
            Counters2.executed.count { it == "barclay" } shouldBe 1
         }
         withClue("gowron") {
            Counters2.executed.count { it == "gowron" } shouldBe 1
         }
      }

      context("picard") {
         Counters2.executed.add(this.testScope.testCase.descriptor.id.value)
         Counters2.threads.add(Thread.currentThread().id)
         context("riker") {
            Counters2.executed.add(this.testScope.testCase.descriptor.id.value)
            Counters2.threads.add(Thread.currentThread().id)
            should("data") {
               Counters2.executed.add(this.testCase.descriptor.id.value)
               Counters2.threads.add(Thread.currentThread().id)
               delay(1000)
            }
            xshould("geordi") {
               error("foo")
            }
            should("lwaxana") {
               Counters2.executed.add(this.testCase.descriptor.id.value)
               Counters2.threads.add(Thread.currentThread().id)
            }
         }
         context("mott") {
            Counters2.executed.add(this.testScope.testCase.descriptor.id.value)
            Counters2.threads.add(Thread.currentThread().id)
            should("ro") {
               Counters2.executed.add(this.testCase.descriptor.id.value)
               Counters2.threads.add(Thread.currentThread().id)
            }
            context("obrien") {
               Counters2.executed.add(this.testScope.testCase.descriptor.id.value)
               Counters2.threads.add(Thread.currentThread().id)
               should("barclay") {
                  Counters2.executed.add(this.testCase.descriptor.id.value)
                  Counters2.threads.add(Thread.currentThread().id)
               }
               should("gowron") {
                  Counters2.executed.add(this.testCase.descriptor.id.value)
                  Counters2.threads.add(Thread.currentThread().id)
               }
            }
            should("pulaski") {
               Counters2.executed.add(this.testCase.descriptor.id.value)
            }
         }
         context("crusher") {
            Counters2.executed.add(this.testScope.testCase.descriptor.id.value)
            Counters2.threads.add(Thread.currentThread().id)
            should("troi") {
               Counters2.executed.add(this.testCase.descriptor.id.value)
               Counters2.threads.add(Thread.currentThread().id)
            }
            should("yar") {
               Counters2.executed.add(this.testCase.descriptor.id.value)
               Counters2.threads.add(Thread.currentThread().id)
            }
            xshould("alexander") {
               error("foo")
            }
            should("hugh") {
               Counters2.executed.add(this.testCase.descriptor.id.value)
               Counters2.threads.add(Thread.currentThread().id)
            }
         }
      }
      context("q") {
         Counters2.executed.add(this.testScope.testCase.descriptor.id.value)
         Counters2.threads.add(Thread.currentThread().id)
         should("wesley") {
            Counters2.executed.add(this.testCase.descriptor.id.value)
            Counters2.threads.add(Thread.currentThread().id)
         }
         should("worf") {
            Counters2.executed.add(this.testCase.descriptor.id.value)
            Counters2.threads.add(Thread.currentThread().id)
         }
         should("lore") {
            Counters2.executed.add(this.testCase.descriptor.id.value)
            Counters2.threads.add(Thread.currentThread().id)
            delay(1000)
         }
      }
      context("kehler") {
         Counters2.executed.add(this.testScope.testCase.descriptor.id.value)
         Counters2.threads.add(Thread.currentThread().id)
         should("keiko") {
            Counters2.executed.add(this.testCase.descriptor.id.value)
            Counters2.threads.add(Thread.currentThread().id)
         }
      }
   }
}
