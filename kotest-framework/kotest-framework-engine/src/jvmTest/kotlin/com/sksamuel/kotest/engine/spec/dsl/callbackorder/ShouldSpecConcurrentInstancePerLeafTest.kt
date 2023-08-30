package com.sksamuel.kotest.engine.spec.dsl.callbackorder//package com.sksamuel.kotest.specs.shouldspec
//
//import io.kotest.assertions.withClue
//import io.kotest.core.spec.IsolationMode
//import io.kotest.core.spec.style.ShouldSpec
//import io.kotest.core.test.TestCase
//import io.kotest.matchers.shouldBe
//import kotlinx.coroutines.delay
//
//object Counters {
//   var specs = mutableSetOf<Int>()
//   var executed = mutableListOf<String>()
//   var threads = mutableSetOf<Long>()
//}
//
//class ShouldSpecConcurrentInstancePerLeafTest : ShouldSpec() {
//
//   override fun isolationMode() = IsolationMode.InstancePerLeaf
//
//   override fun threads() = 3
//
//   override fun beforeTest(testCase: TestCase) {
//      synchronized(Counters) {
//         Counters.specs.add(testCase.spec.hashCode())
//      }
//   }
//
//   init {
//
//      afterProject {
//         Counters.specs.size shouldBe 15
//         // should be 3 because we created 3 dispatchers
//         Counters.threads.size shouldBe 3
//         withClue("riker") {
//            Counters.executed.count { it == "riker" } shouldBe 3
//         }
//         withClue("data") {
//            Counters.executed.count { it == "data" } shouldBe 1
//         }
//         withClue("lwaxana") {
//            Counters.executed.count { it == "lwaxana" } shouldBe 1
//         }
//         withClue("crusher") {
//            Counters.executed.count { it == "crusher" } shouldBe 4
//         }
//         withClue("worf") {
//            Counters.executed.count { it == "worf" } shouldBe 1
//         }
//         withClue("keiko") {
//            Counters.executed.count { it == "keiko" } shouldBe 1
//         }
//         withClue("mott") {
//            Counters.executed.count { it == "mott" } shouldBe 4
//         }
//         withClue("ro") {
//            Counters.executed.count { it == "ro" } shouldBe 1
//         }
//         withClue("obrien") {
//            Counters.executed.count { it == "obrien" } shouldBe 2
//         }
//         withClue("barclay") {
//            Counters.executed.count { it == "barclay" } shouldBe 1
//         }
//         withClue("gowron") {
//            Counters.executed.count { it == "gowron" } shouldBe 1
//         }
//      }
//
//      context("picard") {
//         synchronized(Counters) { Counters.executed.add(this.testScope.testCase.descriptor.id.value) }
//         synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//         context("riker") {
//            synchronized(Counters) { Counters.executed.add(this.testScope.testCase.descriptor.id.value) }
//            synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            should("data") {
//               synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//               synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//               delay(1000)
//            }
//            xshould("geordi") {
//               error("foo")
//            }
//            should("lwaxana") {
//               synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//               synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            }
//         }
//         context("mott") {
//            synchronized(Counters) { Counters.executed.add(this.testScope.testCase.descriptor.id.value) }
//            synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            should("ro") {
//               synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//               synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            }
//            context("obrien") {
//               synchronized(Counters) { Counters.executed.add(this.testScope.testCase.descriptor.id.value) }
//               synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//               should("barclay") {
//                  synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//                  synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//               }
//               should("gowron") {
//                  synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//                  synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//               }
//            }
//            should("pulaski") {
//               synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//               synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            }
//         }
//         context("crusher") {
//            synchronized(Counters) { Counters.executed.add(this.testScope.testCase.descriptor.id.value) }
//            synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            should("troi") {
//               synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//               synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            }
//            should("yar") {
//               synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//               synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            }
//            xshould("alexander") {
//               error("foo")
//            }
//            should("hugh") {
//               synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//               synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            }
//         }
//      }
//      context("q") {
//         synchronized(Counters) { Counters.executed.add(this.testScope.testCase.descriptor.id.value) }
//         synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//         should("wesley") {
//            synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//            synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//         }
//         should("worf") {
//            synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//            synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//         }
//         should("lore") {
//            synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//            synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//            delay(1000)
//         }
//      }
//      context("kehler") {
//         synchronized(Counters) { Counters.executed.add(this.testScope.testCase.descriptor.id.value) }
//         synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//         should("keiko") {
//            synchronized(Counters) { Counters.executed.add(this.testCase.descriptor.id.value) }
//            synchronized(Counters) { Counters.threads.add(Thread.currentThread().id) }
//         }
//      }
//   }
//}
