package io.kotest.core.specs

import io.kotest.*
import io.kotest.core.AssertionMode
import io.kotest.core.IsolationMode
import io.kotest.core.TestCaseOrder
import io.kotest.core.tags.Tag
import io.kotest.extensions.TestListener

val stringTest = stringSpec {
   "a test" {
      1 shouldBe 2
   }
}

val mytag = Tag("mytag")

val mylistener = object : TestListener {}

val funTests = funSpec {

   beforeTest {
      println("Starting test case ${it.name}")
   }

   afterTest { testCase, result ->
      println(testCase.name + " is completed with result " + result)
   }

   beforeAll {
      println("Starting this example")
   }

   afterAll {
      println("Finished this example")
   }

   isolationMode = IsolationMode.InstancePerLeaf

   assertionMode = AssertionMode.Error

   testCaseOrder = TestCaseOrder.Random

   tags(mytag)

   listeners(mylistener)

   test("my test") {
      1 + 1 shouldBe 2
   }

   include(stringTest)
}

class MyFunSpec : FunSpec() {

   init {

      isolationMode = IsolationMode.InstancePerLeaf

      test("test 1") {
         1 + 1 shouldBe 2
      }

      include(funTests)
   }
}
