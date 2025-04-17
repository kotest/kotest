package io.kotest.engine.stable

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.Row3
import io.kotest.matchers.shouldBe
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class StableIdentsTest : FunSpec({

   test("null should be stable") {
      StableIdents.getStableIdentifier(null) shouldBe "<null>"
   }

   test("generic platform types should be stable") {
      StableIdents.getStableIdentifier("string") shouldBe "string"
      StableIdents.getStableIdentifier(123) shouldBe "123"
      StableIdents.getStableIdentifier(123L) shouldBe "123"
      StableIdents.getStableIdentifier(charArrayOf('a', 'z')) shouldBe "az"
      StableIdents.getStableIdentifier(byteArrayOf(1, 2, 3)) shouldBe "1,2,3"
      StableIdents.getStableIdentifier(shortArrayOf(1, 2, 3)) shouldBe "1,2,3"
      StableIdents.getStableIdentifier(intArrayOf(1, 3, 5)) shouldBe "1,3,5"
      StableIdents.getStableIdentifier(longArrayOf(1, 3, 5)) shouldBe "1,3,5"
      StableIdents.getStableIdentifier(doubleArrayOf(1.0, 3.0, 5.0)) shouldBe "1.0,3.0,5.0"
      StableIdents.getStableIdentifier(floatArrayOf(1.0f, 3.0f, 5.0f)) shouldBe "1.0,3.0,5.0"
      StableIdents.getStableIdentifier(booleanArrayOf(true, false, true)) shouldBe "true,false,true"
      StableIdents.getStableIdentifier(15.seconds) shouldBe "15s"
      StableIdents.getStableIdentifier("[a-z]".toRegex()) shouldBe "[a-z]"
      StableIdents.getStableIdentifier(String::class) shouldBe "kotlin.String"
      StableIdents.getStableIdentifier(1u) shouldBe "1"
      StableIdents.getStableIdentifier(1UL) shouldBe "1"
      StableIdents.getStableIdentifier(Unit) shouldBe "Unit"

      val ubyte: UByte = 1u
      StableIdents.getStableIdentifier(ubyte) shouldBe "1"
   }

   test("data class with stable members should be correctly identified as stable") {
      data class StableClass(
         val string: String,
         val int: Int,
         val long: Long,
         val double: Double,
         val float: Float,
         val byte: Byte,
         val short: Short,
         val boolean: Boolean,
         val char: Char,
         val unit: Unit,
         val duration: Duration,
         val ub: UByte,
         val us: UShort,
         val ul: ULong,
         val regex: Regex,
         val kclass: KClass<String>,
      )

      class Unstable() {
         // something unstable here
      }

      data class UnstableDataClass(val string: String, val unstable: Unstable)

      StableIdents.isStable(StableClass::class) shouldBe true
      StableIdents.isStable(Unstable::class) shouldBe false
      StableIdents.isStable(UnstableDataClass::class) shouldBe false
   }

   test("UUIDs should be stable on JVM") {
      StableIdents.isStable(UUID::class) shouldBe true
   }

   test("data classes containing UUIDs should be stable on JVM") {
      data class Foo(val uuid: UUID)
      StableIdents.isStable(Foo::class) shouldBe true
   }

   test("data class where function and field have same name") {
      data class ExoticClass(val a: Int, val b: String) {
         fun b(value: String) = copy(b = value)
      }

      val edgeCase = ExoticClass(1, "a")
      StableIdents.isStable(ExoticClass::class, edgeCase) shouldBe true
   }

   context("Given a data class with generics") {
      test("When all members are stable, then the class should be considered stable") {
         val x = Row3("x", 1, 2.0)
         StableIdents.isStable(x::class, x) shouldBe true
      }

      test("When isStable does not have access to actual values, it cannot determine the stability of the class") {
         val x = Row3("x", 1, 2.0)
         StableIdents.isStable(x::class, t = null) shouldBe false
      }

      test("When an unstable type is included, the entire data class is considered unstable, even when including values") {
         val y: Row3<String, Int, Array<Int>> = Row3("x", 1, arrayOf(1, 2))
         StableIdents.isStable(y::class, y) shouldBe false
      }

      test("When layering generic data classes, it should still work") {
         val stable = Pair(Pair("a", "b"), Pair("c", "d"))
         StableIdents.isStable(stable::class, stable) shouldBe true

         val unstable = Pair(Pair("a", "b"), Pair("c", arrayOf(1, 2)))
         StableIdents.isStable(unstable::class, unstable) shouldBe false
      }

      test("When class is private, it should still work") {
         val foo = Foo("bar")
         StableIdents.isStable(foo::class, foo) shouldBe true
      }
   }

})

private data class Foo(val bar: String)
