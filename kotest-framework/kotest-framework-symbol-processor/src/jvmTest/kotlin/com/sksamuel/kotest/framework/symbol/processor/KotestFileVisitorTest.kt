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

   context("isSpec should detect subclasses of Spec via any spec style") {
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
         // models com.sksamuel.MySpec -> <spec style> -> io.kotest.core.spec.Spec
         KotestFileVisitor().isSpec(
            SimpleKSClassDeclaration(
               className = "com.sksamuel.MySpec",
               supers = listOf(
                  SimpleKSTypeReference(
                     SimpleKSType(
                        declaration = SimpleKSClassDeclaration(
                           className = type.qualifiedName!!,
                           supers = listOf(
                              SimpleKSTypeReference(
                                 SimpleKSType(
                                    declaration = SimpleKSClassDeclaration(
                                       className = "io.kotest.core.spec.Spec",
                                    )
                                 )
                              )
                           ),
                        )
                     )
                  )
               ),
            )
         ) shouldBe true
      }
   }

   test("isSpec should detect a direct subclass of Spec") {
      KotestFileVisitor().isSpec(
         SimpleKSClassDeclaration(
            className = "com.sksamuel.MySpec",
            supers = listOf(
               SimpleKSTypeReference(
                  SimpleKSType(
                     declaration = SimpleKSClassDeclaration(
                        className = "io.kotest.core.spec.Spec",
                     )
                  )
               )
            ),
         )
      ) shouldBe true
   }

   test("isSpec should return false for classes that do not extend Spec") {
      KotestFileVisitor().isSpec(
         SimpleKSClassDeclaration(
            className = "com.sksamuel.NotASpec",
            supers = listOf(
               SimpleKSTypeReference(
                  SimpleKSType(
                     declaration = SimpleKSClassDeclaration(
                        className = "kotlin.Any",
                     )
                  )
               )
            ),
         )
      ) shouldBe false
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

   test("isAbstract returns true for abstract spec classes") {
      KotestFileVisitor().isAbstract(
         SimpleKSClassDeclaration(
            className = "com.sksamuel.AbstractSpec",
            modifiers = setOf(Modifier.ABSTRACT),
         )
      ) shouldBe true
   }

   test("isAbstract returns true for sealed spec classes") {
      KotestFileVisitor().isAbstract(
         SimpleKSClassDeclaration(
            className = "com.sksamuel.SealedSpec",
            modifiers = setOf(Modifier.SEALED),
         )
      ) shouldBe true
   }

   test("isAbstract returns false for plain classes") {
      KotestFileVisitor().isAbstract(
         SimpleKSClassDeclaration(
            className = "com.sksamuel.MySpec",
         )
      ) shouldBe false
   }

   test("abstract spec subclasses are excluded from visitor.specs") {
      val visitor = KotestFileVisitor()
      visitor.visitClassDeclaration(
         SimpleKSClassDeclaration(
            className = "com.sksamuel.AbstractSpec",
            supers = listOf(
               SimpleKSTypeReference(
                  SimpleKSType(
                     declaration = SimpleKSClassDeclaration(
                        className = FunSpec::class.qualifiedName!!,
                        supers = listOf(
                           SimpleKSTypeReference(
                              SimpleKSType(
                                 declaration = SimpleKSClassDeclaration(
                                    className = "io.kotest.core.spec.Spec",
                                 )
                              )
                           )
                        ),
                     )
                  )
               )
            ),
            modifiers = setOf(Modifier.ABSTRACT),
         ),
         Unit
      )
      visitor.specs shouldBe emptyList()
   }

   test("specs are not duplicated when the same class is visited in multiple ksp rounds") {
      val spec = SimpleKSClassDeclaration(
         className = "com.sksamuel.MySpec",
         supers = listOf(
            SimpleKSTypeReference(
               SimpleKSType(
                  declaration = SimpleKSClassDeclaration(
                     className = "io.kotest.core.spec.Spec",
                  )
               )
            )
         ),
      )
      val visitor = KotestFileVisitor()
      // ksp invokes process() once per round, which can visit the same declaration again
      visitor.visitClassDeclaration(spec, Unit)
      visitor.visitClassDeclaration(spec, Unit)
      visitor.specs.map { it.qualifiedName?.asString() } shouldBe listOf("com.sksamuel.MySpec")
   }

   test("configs are not duplicated when the same class is visited in multiple ksp rounds") {
      val config = SimpleKSClassDeclaration(
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
      val visitor = KotestFileVisitor()
      visitor.visitClassDeclaration(config, Unit)
      visitor.visitClassDeclaration(config, Unit)
      visitor.configs.map { it.qualifiedName?.asString() } shouldBe listOf("com.sksamuel.ProjectConfig")
   }
})
