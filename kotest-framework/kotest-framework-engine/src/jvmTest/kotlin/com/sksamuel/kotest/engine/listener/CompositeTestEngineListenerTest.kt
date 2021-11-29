package com.sksamuel.kotest.engine.listener

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.Node
import io.kotest.matchers.shouldBe

class CompositeTestEngineListenerTest : FunSpec({

   test("specStarted should fire for all listeners") {
      var fired1 = false
      var fired2 = false
      val l1 = object : AbstractTestEngineListener() {
         override suspend fun executionStarted(node: Node) {
            if (node is Node.Spec) fired1 = true
         }
      }
      val l2 = object : AbstractTestEngineListener() {
         override suspend fun executionStarted(node: Node) {
            if (node is Node.Spec) fired2 = true
         }
      }
      CompositeTestEngineListener(listOf(l1, l2)).executionStarted(Node.Spec(CompositeTestEngineListenerTest::class))
      fired1 shouldBe true
      fired2 shouldBe true
   }

   test("specFinished should fire for all listeners") {
      var fired1 = false
      var fired2 = false
      val l1 = object : AbstractTestEngineListener() {
         override suspend fun executionFinished(node: Node, result: TestResult) {
            if (node is Node.Spec) fired1 = true
         }
      }
      val l2 = object : AbstractTestEngineListener() {
         override suspend fun executionFinished(node: Node, result: TestResult) {
            if (node is Node.Spec) fired2 = true
         }
      }
      CompositeTestEngineListener(listOf(l1, l2)).executionFinished(
         Node.Spec(CompositeTestEngineListenerTest::class),
         TestResult.success
      )
      fired1 shouldBe true
      fired2 shouldBe true
   }
})
