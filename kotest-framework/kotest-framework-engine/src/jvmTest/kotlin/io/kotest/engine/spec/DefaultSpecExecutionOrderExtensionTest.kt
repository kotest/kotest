package io.kotest.engine.spec

import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DefaultSpecExecutionOrderExtensionTest : DescribeSpec({

   describe("The DefaultSpecExecutionOrder extension should support") {
      it("SpecExecutionOrder.Undefined") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Undefined).sortClasses(
            listOf(Spec1::class, Spec2::class, Spec3::class, Spec4::class)
         ) shouldBe listOf(Spec1::class, Spec2::class, Spec3::class, Spec4::class)
      }
   }

})

private class Spec1 : FunSpec()
private class Spec2 : FunSpec()
private class Spec3 : FunSpec()
private class Spec4 : FunSpec()
