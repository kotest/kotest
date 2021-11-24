package com.sksamuel.kotest.engine.spec.sorts

import io.kotest.core.spec.Order
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.DefaultSpecExecutionOrderExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class DefaultSpecExecutionOrderExtensionTest : DescribeSpec({

   describe("The DefaultSpecExecutionOrder extension should support") {

      it("SpecExecutionOrder.Undefined") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Undefined).sort(
            listOf(
               SpecRef.Reference(ASpec::class),
               SpecRef.Reference(ZSpec::class),
               SpecRef.Reference(SpecA::class),
               SpecRef.Reference(SpecZ::class),
            )
         ) shouldBe listOf(
            SpecRef.Reference(ASpec::class),
            SpecRef.Reference(ZSpec::class),
            SpecRef.Reference(SpecA::class),
            SpecRef.Reference(SpecZ::class),
         )
      }

      it("SpecExecutionOrder.Annotated") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Annotated).sort(
            listOf(
               SpecRef.Reference(ASpec::class),
               SpecRef.Reference(ZSpec::class),
               SpecRef.Reference(SpecA::class),
               SpecRef.Reference(SpecZ::class),
            )
         ) shouldBe listOf(
            SpecRef.Reference(SpecA::class),
            SpecRef.Reference(ASpec::class),
            SpecRef.Reference(ZSpec::class),
            SpecRef.Reference(SpecZ::class),
         )
      }

      it("SpecExecutionOrder.Lexicographic") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Lexicographic).sort(
            listOf(
               SpecRef.Reference(ASpec::class),
               SpecRef.Reference(ZSpec::class),
               SpecRef.Reference(SpecA::class),
               SpecRef.Reference(SpecZ::class),
            )
         ) shouldBe listOf(
            SpecRef.Reference(ASpec::class),
            SpecRef.Reference(SpecA::class),
            SpecRef.Reference(SpecZ::class),
            SpecRef.Reference(ZSpec::class),
         )
      }

      it("SpecExecutionOrder.Random") {
         // should have all combinations since it's meant to be random
         List(10000) {
            DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Random).sort(
               listOf(
                  SpecRef.Reference(ASpec::class),
                  SpecRef.Reference(ZSpec::class),
                  SpecRef.Reference(SpecA::class),
                  SpecRef.Reference(SpecZ::class),
               )
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
