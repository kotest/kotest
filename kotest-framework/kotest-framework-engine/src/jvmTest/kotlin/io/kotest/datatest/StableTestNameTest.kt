package io.kotest.datatest

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.mpp.isStable
import java.util.UUID

@EnabledIf(LinuxCondition::class)
class StableTestNameTest : FunSpec() {
   init {
      test("UUIDs should be stable on JVM") {
         isStable(UUID::class) shouldBe true
      }
      test("data classes containing UUIDs should be stable on JVM") {
         data class Foo(val uuid: UUID)
         isStable(Foo::class) shouldBe true
      }
   }
}
