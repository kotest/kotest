package com.sksamuel.kotest.engine.spec.sorts

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.Order
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.spec.DefaultSpecExecutionOrderExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class DefaultSpecExecutionOrderExtensionTest : DescribeSpec({

   describe("The DefaultSpecExecutionOrder extension should support") {

      it("SpecExecutionOrder.Undefined") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Undefined, ProjectConfigResolver()).sort(
            listOf(
               SpecRef.Reference(ZSpec::class),
               SpecRef.Reference(SpecZ::class),
            )
         ) shouldBe listOf(
            SpecRef.Reference(ZSpec::class),
            SpecRef.Reference(SpecZ::class),
         )
      }

      it("SpecExecutionOrder.Annotated") {
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Annotated, ProjectConfigResolver()).sort(
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
         DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Lexicographic, ProjectConfigResolver()).sort(
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
            DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Random, ProjectConfigResolver()).sort(
               listOf(
                  SpecRef.Reference(ASpec::class),
                  SpecRef.Reference(ZSpec::class),
                  SpecRef.Reference(SpecA::class),
                  SpecRef.Reference(SpecZ::class),
               )
            )
         }.distinct().shouldHaveSize(24)
      }

      it("SpecExecutionOrder.Random should use seed") {

         val c = object : AbstractProjectConfig() {
            override val randomOrderSeed = 123123123L
         }

         val specs = listOf(
            SpecRef.Reference(ASpec::class),
            SpecRef.Reference(ZSpec::class),
            SpecRef.Reference(SpecA::class),
            SpecRef.Reference(SpecZ::class),
         )

         val ext = DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Random, ProjectConfigResolver(c))
         ext.sort(specs) shouldBe ext.sort(specs)
      }

      it("should error if mode is undefined and a spec is annotated") {
         val specs = listOf(
            SpecRef.Reference(ASpec::class),
            SpecRef.Reference(ZSpec::class),
         )
         shouldThrowAny {
            DefaultSpecExecutionOrderExtension(SpecExecutionOrder.Undefined, ProjectConfigResolver()).sort(specs)
         }
      }
   }

})

@Order(3)
private class ASpec : FunSpec()

private class ZSpec : FunSpec()

@Order(2)
private class SpecA : FunSpec()

private class SpecZ : FunSpec()
