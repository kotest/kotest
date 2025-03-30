package io.kotest.engine.concurrency

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.Parallel
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.isIsolate
import io.kotest.engine.isParallel
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

@Parallel
private class Boo

@Parallel
private annotation class RunParallel

@RunParallel
private class Gaz

@Parallel
private abstract class Woo

private class Waz : Woo()

@EnabledIf(LinuxOnlyGithubCondition::class)
class ConcurrencyAnnotationTests : FreeSpec({

   "isIsolate should return true for class that is directly annotated by Isolate" {
      Foo::class.isIsolate() shouldBe true
   }

   "isIsolate should return true for class that has an annotation which includes Isolate" {
      Bar::class.isIsolate() shouldBe true
   }

   "isIsolate should return true for class which inherited from class that include Isolate" {
      Qux::class.isIsolate() shouldBe true
   }

   "isParallel should return true for class that is directly annotated by Parallel" {
      Boo::class.isParallel() shouldBe true
   }

   "isParallel should return true for class that has an annotation which includes Parallel" {
      Gaz::class.isParallel() shouldBe true
   }

   "isParallel should return true for class which inherited from class that include Parallel" {
      Waz::class.isParallel() shouldBe true
   }

})
