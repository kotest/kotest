package com.sksamuel.kotest

import io.kotest.assertions.errorCollector
import io.kotest.assertions.orThat
import io.kotest.assertions.orThis
import io.kotest.assertions.shouldBeEither
import io.kotest.assertions.shouldBeThis
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.string.endWith
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.startWith
import java.math.BigInteger

private const val helloThere = "hello there"

class OrTests : FunSpec({
   test("if both aren't equal shouldBeEither and shouldBeThis should fail") {
      listOf(
         shouldFail { 1 shouldBeEither (2 orThis 3) }.message,
         shouldFail { 1 shouldBeEither (3 orThis 2) }.message,
         shouldFail { 1 shouldBeThis 2 orThat 3 }.message,
         shouldFail { 1 shouldBeThis 3 orThat 2 }.message,
      ).forEach {
         it.shouldContain("The following 2 assertions failed:")
         it.shouldContain("""[1-2]\) expected:<2> but was:<1>""".toRegex())
         it.shouldContain("""[1-2]\) expected:<3> but was:<1>""".toRegex())
      }
   }

   test("if both matchers fail shouldBeEither and shouldBeThis should fail") {
      val a = startWith("there")
      val b = endWith("hello")
      listOf(
         shouldFail { helloThere shouldBeEither (a orThis b) }.message,
         shouldFail { helloThere shouldBeEither (b orThis a) }.message,
         shouldFail { helloThere shouldBeThis a orThat b }.message,
         shouldFail { helloThere shouldBeThis b orThat a }.message,
      ).forEach {
         it.shouldContain("The following 2 assertions failed:")
         it.shouldContain("""[1-2]\) "hello there" should start with "there" \(diverged at index 0\)""".toRegex())
         it.shouldContain("""[1-2]\) "hello there" should end with "hello"""".toRegex())
      }
   }

   test ("if only one matcher fails shouldBeEither and shouldBeThis should succeed") {
      val a = startWith("hello")
      val b = endWith("blah blah blah")

      helloThere shouldBeEither (a orThis b)
      errorCollector.errors().shouldBeEmpty()

      helloThere shouldBeEither (b orThis a)
      errorCollector.errors().shouldBeEmpty()

      helloThere shouldBeThis a orThat b
      errorCollector.errors().shouldBeEmpty()

      helloThere shouldBeThis b orThat a
      errorCollector.errors().shouldBeEmpty()
   }

   test("if only one item equals shouldBeEither and shouldBeThis should succeed") {
      val a = 42.0
      val b = BigInteger.TEN

      42F shouldBeEither (a orThis b)
      errorCollector.errors().shouldBeEmpty()

      42F shouldBeEither (b orThis a)
      errorCollector.errors().shouldBeEmpty()

      42F shouldBeThis a orThat b
      errorCollector.errors().shouldBeEmpty()

      42F shouldBeThis b orThat a
      errorCollector.errors().shouldBeEmpty()
   }
})
