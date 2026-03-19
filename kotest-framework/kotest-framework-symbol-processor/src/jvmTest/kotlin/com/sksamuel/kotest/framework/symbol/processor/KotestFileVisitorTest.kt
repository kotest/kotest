package com.sksamuel.kotest.framework.symbol.processor

import com.google.devtools.ksp.symbol.Modifier
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.datatest.withTests
import io.kotest.framework.symbol.processor.KotestFileVisitor
import io.kotest.matchers.shouldBe

class KotestFileVisitorTest : FunSpec({

   context("isSpec should detect all spec types") {
      withTests(
         FunSpec::class,
         DescribeSpec::class,
         ShouldSpec::class,
         ExpectSpec::class,
         FeatureSpec::class,
         WordSpec::class,
         FreeSpec::class,
         StringSpec::class,
         BehaviorSpec::class
      ) { type ->
         KotestFileVisitor().isSpec(
            SimpleKSClassDeclaration(
               className = "com.sksamuel.MySpec",
               supers = listOf(
                  SimpleKSTypeReference(
                     SimpleKSType(
                        declaration = SimpleKSClassDeclaration(
                           className = type.qualifiedName!!,
                        )
                     )
                  )
               ),
            )
         ) shouldBe true
      }
   }

   test("isConfig should detect subclasses of AbstractProjectConfig") {
      KotestFileVisitor().isConfig(
         SimpleKSClassDeclaration(
            className = "com.sksamuel.ProjectConfig",
            supers = listOf(
               SimpleKSTypeReference(
                  SimpleKSType(
                     declaration = SimpleKSClassDeclaration(
                        className = "io.kotest.core.config.AbstractProjectConfig",
                     )
                  )
               )
            ),
         )
      ) shouldBe true
   }

   test("should detect private classes") {
      KotestFileVisitor().isPublic(
         SimpleKSClassDeclaration(
            className = "com.sksamuel.MySpec",
            supers = listOf(
               SimpleKSTypeReference(
                  SimpleKSType(
                     declaration = SimpleKSClassDeclaration(
                        className = FunSpec::class.qualifiedName!!,
                     )
                  )
               )
            ),
            modifiers = setOf(Modifier.PRIVATE),
         )
      ) shouldBe false
   }
})
