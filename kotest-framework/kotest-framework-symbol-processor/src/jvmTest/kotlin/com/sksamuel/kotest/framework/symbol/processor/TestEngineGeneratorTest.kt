package com.sksamuel.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import io.kotest.core.spec.style.FunSpec
import io.kotest.framework.symbol.processor.TestEngineGenerator
import io.kotest.matchers.string.shouldContain

class TestEngineGeneratorTest : FunSpec({

   // see https://github.com/kotest/kotest/issues/5168
   test("handle test classes having the same name in different packages") {
      val env = SymbolProcessorEnvironment(
         options = emptyMap(),
         kotlinVersion = KotlinVersion.CURRENT,
         apiVersion = KotlinVersion.CURRENT,
         compilerVersion = KotlinVersion.CURRENT,
         codeGenerator = SimpleCodeGenerator(),
         logger = NoOpKSPLogger,
         platforms = listOf(DefaultJsPlatformInfo),
      )
      val file = TestEngineGenerator(env).createFileSpec(
         specs = listOf(
            SimpleKSClassDeclaration("com.sksamuel.a.MyTestSpec"),
            SimpleKSClassDeclaration("com.sksamuel.b.MyTestSpec"),
            SimpleKSClassDeclaration("com.sksamuel.c.OtherTestSpec"),
         ),
         configs = listOf(SimpleKSClassDeclaration("com.sksamuel.MyConfig")),
      ).toString()

      file shouldContain """import com.sksamuel.a.MyTestSpec as MyTestSpec0"""
      file shouldContain """import com.sksamuel.b.MyTestSpec as MyTestSpec1"""
      file shouldContain """import com.sksamuel.c.OtherTestSpec as OtherTestSpec2"""

      file shouldContain """SpecRef.Function ({ `MyTestSpec0`() }, `MyTestSpec0`::class, "com.sksamuel.a.MyTestSpec")"""
      file shouldContain """SpecRef.Function ({ `MyTestSpec1`() }, `MyTestSpec1`::class, "com.sksamuel.b.MyTestSpec")"""
      file shouldContain """SpecRef.Function ({ `OtherTestSpec2`() }, `OtherTestSpec2`::class, "com.sksamuel.c.OtherTestSpec")"""
   }

   test("should specify config when present") {
      val env = SymbolProcessorEnvironment(
         options = emptyMap(),
         kotlinVersion = KotlinVersion.CURRENT,
         apiVersion = KotlinVersion.CURRENT,
         compilerVersion = KotlinVersion.CURRENT,
         codeGenerator = SimpleCodeGenerator(),
         logger = NoOpKSPLogger,
         platforms = listOf(DefaultJsPlatformInfo),
      )
      val file = TestEngineGenerator(env).createFileSpec(
         specs = listOf(
            SimpleKSClassDeclaration("com.sksamuel.a.MyTestSpec"),
         ),
         configs = listOf(SimpleKSClassDeclaration("com.sksamuel.MyConfig")),
      ).toString()

      file.shouldContain("val config = com.sksamuel.MyConfig()")
   }

   test("handle empty configs") {
      val env = SymbolProcessorEnvironment(
         options = emptyMap(),
         kotlinVersion = KotlinVersion.CURRENT,
         apiVersion = KotlinVersion.CURRENT,
         compilerVersion = KotlinVersion.CURRENT,
         codeGenerator = SimpleCodeGenerator(),
         logger = NoOpKSPLogger,
         platforms = listOf(DefaultJsPlatformInfo),
      )
      val file = TestEngineGenerator(env).createFileSpec(
         specs = listOf(
            SimpleKSClassDeclaration("com.sksamuel.a.MyTestSpec"),
         ),
         configs = emptyList(),
      ).toString()

      file shouldContain """val config = null"""
   }

})

