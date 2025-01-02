package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

private object Counters {
   var specs = mutableSetOf<Int>()
   var executed = mutableListOf<String>()
   var threads = mutableSetOf<Long>()
}

class ShouldSpecInstancePerRootTest : ShouldSpec() {

   override fun isolationMode() = IsolationMode.InstancePerRoot

   override suspend fun beforeTest(testCase: TestCase) {
      Counters.specs.add(testCase.spec.hashCode())
   }

   init {

      afterProject {
         Counters.specs.size shouldBe 3
         Counters.threads.size shouldBe 1
         withClue("riker") {
            Counters.executed.count { it == "riker" } shouldBe 3
         }
         withClue("data") {
            Counters.executed.count { it == "data" } shouldBe 1
         }
         withClue("lwaxana") {
            Counters.executed.count { it == "lwaxana" } shouldBe 1
         }
         withClue("crusher") {
            Counters.executed.count { it == "crusher" } shouldBe 4
         }
         withClue("worf") {
            Counters.executed.count { it == "worf" } shouldBe 1
         }
         withClue("keiko") {
            Counters.executed.count { it == "keiko" } shouldBe 1
         }
         withClue("mott") {
            Counters.executed.count { it == "mott" } shouldBe 4
         }
         withClue("ro") {
            Counters.executed.count { it == "ro" } shouldBe 1
         }
         withClue("obrien") {
            Counters.executed.count { it == "obrien" } shouldBe 2
         }
         withClue("barclay") {
            Counters.executed.count { it == "barclay" } shouldBe 1
         }
         withClue("gowron") {
            Counters.executed.count { it == "gowron" } shouldBe 1
         }
      }

      context("picard") {
         Counters.executed.add(this.testScope.testCase.descriptor.id.value)
         Counters.threads.add(Thread.currentThread().id)
         context("riker") {
            Counters.executed.add(this.testScope.testCase.descriptor.id.value)
            Counters.threads.add(Thread.currentThread().id)
            should("data") {
               Counters.executed.add(this.testCase.descriptor.id.value)
               Counters.threads.add(Thread.currentThread().id)
               delay(1000)
            }
            xshould("geordi") {
               error("foo")
            }
            should("lwaxana") {
               Counters.executed.add(this.testCase.descriptor.id.value)
               Counters.threads.add(Thread.currentThread().id)
            }
         }
         context("mott") {
            Counters.executed.add(this.testScope.testCase.descriptor.id.value)
            Counters.threads.add(Thread.currentThread().id)
            should("ro") {
               Counters.executed.add(this.testCase.descriptor.id.value)
               Counters.threads.add(Thread.currentThread().id)
            }
            context("obrien") {
               Counters.executed.add(this.testScope.testCase.descriptor.id.value)
               Counters.threads.add(Thread.currentThread().id)
               should("barclay") {
                  Counters.executed.add(this.testCase.descriptor.id.value)
                  Counters.threads.add(Thread.currentThread().id)
               }
               should("gowron") {
                  Counters.executed.add(this.testCase.descriptor.id.value)
                  Counters.threads.add(Thread.currentThread().id)
               }
            }
            should("pulaski") {
               Counters.executed.add(this.testCase.descriptor.id.value)
            }
         }
         context("crusher") {
            Counters.executed.add(this.testScope.testCase.descriptor.id.value)
            Counters.threads.add(Thread.currentThread().id)
            should("troi") {
               Counters.executed.add(this.testCase.descriptor.id.value)
               Counters.threads.add(Thread.currentThread().id)
            }
            should("yar") {
               Counters.executed.add(this.testCase.descriptor.id.value)
               Counters.threads.add(Thread.currentThread().id)
            }
            xshould("alexander") {
               error("foo")
            }
            should("hugh") {
               Counters.executed.add(this.testCase.descriptor.id.value)
               Counters.threads.add(Thread.currentThread().id)
            }
         }
      }
      context("q") {
         Counters.executed.add(this.testScope.testCase.descriptor.id.value)
         Counters.threads.add(Thread.currentThread().id)
         should("wesley") {
            Counters.executed.add(this.testCase.descriptor.id.value)
            Counters.threads.add(Thread.currentThread().id)
         }
         should("worf") {
            Counters.executed.add(this.testCase.descriptor.id.value)
            Counters.threads.add(Thread.currentThread().id)
         }
         should("lore") {
            Counters.executed.add(this.testCase.descriptor.id.value)
            Counters.threads.add(Thread.currentThread().id)
            delay(1000)
         }
      }
      context("kehler") {
         Counters.executed.add(this.testScope.testCase.descriptor.id.value)
         Counters.threads.add(Thread.currentThread().id)
         should("keiko") {
            Counters.executed.add(this.testCase.descriptor.id.value)
            Counters.threads.add(Thread.currentThread().id)
         }
      }
   }
}
