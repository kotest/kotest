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
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Undefined).sort(
            listOf(
               ReflectiveSpecRef(ASpec::class),
               ReflectiveSpecRef(ZSpec::class),
               ReflectiveSpecRef(SpecA::class),
               ReflectiveSpecRef(SpecZ::class),
            )
         ) shouldBe listOf(
            ReflectiveSpecRef(ASpec::class),
            ReflectiveSpecRef(ZSpec::class),
            ReflectiveSpecRef(SpecA::class),
            ReflectiveSpecRef(SpecZ::class),
         )
      }

      it("SpecExecutionOrder.Annotated") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Annotated).sort(
            listOf(
               ReflectiveSpecRef(ASpec::class),
               ReflectiveSpecRef(ZSpec::class),
               ReflectiveSpecRef(SpecA::class),
               ReflectiveSpecRef(SpecZ::class),
            )
         ) shouldBe listOf(
            ReflectiveSpecRef(SpecA::class),
            ReflectiveSpecRef(ASpec::class),
            ReflectiveSpecRef(ZSpec::class),
            ReflectiveSpecRef(SpecZ::class),
         )
      }

      it("SpecExecutionOrder.Lexicographic") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Lexicographic).sort(
            listOf(
               ReflectiveSpecRef(ASpec::class),
               ReflectiveSpecRef(ZSpec::class),
               ReflectiveSpecRef(SpecA::class),
               ReflectiveSpecRef(SpecZ::class),
            )
         ) shouldBe listOf(
            ReflectiveSpecRef(ASpec::class),
            ReflectiveSpecRef(SpecA::class),
            ReflectiveSpecRef(SpecZ::class),
            ReflectiveSpecRef(ZSpec::class),
         )
      }

      it("SpecExecutionOrder.Random") {
         // should have all combinations since it's meant to be random
         List(10000) {
            DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Random).sort(
               listOf(
                  ReflectiveSpecRef(ASpec::class),
                  ReflectiveSpecRef(ZSpec::class),
                  ReflectiveSpecRef(SpecA::class),
                  ReflectiveSpecRef(SpecZ::class),
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
