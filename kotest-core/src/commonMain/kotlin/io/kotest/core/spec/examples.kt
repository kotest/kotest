package io.kotest.core.spec

import io.kotest.core.AssertionMode
import io.kotest.core.Tag
import io.kotest.extensions.TestListener
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.shouldBe

val mytag = Tag("mytag")

val mylistener = object : TestListener {}

val stringTests = stringSpec {
   "foo" {
      "a".shouldHaveLength(1)
   }
}

val funTests = funSpec {

   beforeTest {
      println("Starting test case ${it.name}")
   }

   afterTest { testCase, result ->
      println(testCase.name + " is completed with result " + result)
   }

   beforeSpec {
      println("Starting this example")
   }

   afterSpec {
      println("Finished this example")
   }

   assertionMode = AssertionMode.Error

   tags(mytag)

   listeners(mylistener)

   test("bar") {
      1 + 1 shouldBe 2
   }
}
