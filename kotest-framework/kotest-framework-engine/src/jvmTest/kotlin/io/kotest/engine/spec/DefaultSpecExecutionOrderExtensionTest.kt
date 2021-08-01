package io.kotest.engine.spec

import io.kotest.core.spec.Order
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class DefaultSpecExecutionOrderExtensionTest : DescribeSpec({

   describe("The DefaultSpecExecutionOrder extension should support") {

      it("SpecExecutionOrder.Undefined") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Undefined).sortClasses(
            listOf(ASpec::class, ZSpec::class, SpecA::class, SpecZ::class)
         ) shouldBe listOf(ASpec::class, ZSpec::class, SpecA::class, SpecZ::class)
      }

      it("SpecExecutionOrder.Annotated") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Annotated).sortClasses(
            listOf(ASpec::class, ZSpec::class, SpecA::class, SpecZ::class)
         ) shouldBe listOf(SpecA::class, ASpec::class, ZSpec::class, SpecZ::class)
      }

      it("SpecExecutionOrder.Lexicographic") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Lexicographic).sortClasses(
            listOf(ASpec::class, ZSpec::class, SpecA::class, SpecZ::class)
         ) shouldBe listOf(ASpec::class, SpecA::class, SpecZ::class, ZSpec::class)
      }

      it("SpecExecutionOrder.Random") {
         // should have all combinations since it's meant to be random
         List(10000) {
            DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Random).sortClasses(
               listOf(ASpec::class, ZSpec::class, SpecA::class, SpecZ::class)
            )
         }.distinct().shouldHaveSize(24)
      }
   }

})

@Order(3)
private class ASpec : FunSpec()

private class ZSpec : FunSpec()

@Order(2)
private class SpecA : FunSpec()

private class SpecZ : FunSpec()
