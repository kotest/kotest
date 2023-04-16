package io.kotest.engine.concurrency

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@Isolate
private class Foo

@Isolate
private annotation class RunIsolated

@RunIsolated
private class Bar

@Isolate
private abstract class Baz

private class Qux : Baz()

class ConcurrencyKtTest : FreeSpec({

   "isIsolate should return true for class that directly marked by Isolate" {
      Foo::class.isIsolate() shouldBe true
   }

   "isIsolate should return true for class that marked by annotation which include Isolate" {
      Bar::class.isIsolate() shouldBe true
   }

   "isIsolate should return true for class which inherited from class that include Isolate" {
      Qux::class.isIsolate() shouldBe true
   }

})
