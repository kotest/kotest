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
            SimpleKSClassDeclaration("com.sksamuel.a.MyTestSpec",),
            SimpleKSClassDeclaration("com.sksamuel.b.MyTestSpec",),
            SimpleKSClassDeclaration("com.sksamuel.c.OtherTestSpec",),
         ),
         configs = listOf(SimpleKSClassDeclaration("com.sksamuel.MyConfig",)),
      ).toString()

      file shouldContain """import com.sksamuel.a.MyTestSpec as MyTestSpec_0"""
      file shouldContain """import com.sksamuel.b.MyTestSpec as MyTestSpec_1"""
      file shouldContain """import com.sksamuel.c.OtherTestSpec as OtherTestSpec_2"""

      file shouldContain """SpecRef.Function ({ `MyTestSpec_0`() }, `MyTestSpec_0`::class, "com.sksamuel.a.MyTestSpec")"""
      file shouldContain """SpecRef.Function ({ `MyTestSpec_1`() }, `MyTestSpec_1`::class, "com.sksamuel.b.MyTestSpec")"""
      file shouldContain """SpecRef.Function ({ `OtherTestSpec_2`() }, `OtherTestSpec_2`::class, "com.sksamuel.c.OtherTestSpec")"""
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
            SimpleKSClassDeclaration("com.sksamuel.a.MyTestSpec",),
         ),
         configs = listOf(SimpleKSClassDeclaration("com.sksamuel.MyConfig",)),
      ).toString()

      file.shouldContain("val config = com.sksamuel.MyConfig()")
   }

   // see https://github.com/kotest/kotest/issues/5621
   // Without an underscore separator, "Spec1" at index 0 produces alias "Spec10",
   // which clashes with "Spec" at index 10 also producing "Spec10".
   test("import alias underscore separator prevents clash between spec name digits and index digits") {
      val env = SymbolProcessorEnvironment(
         options = emptyMap(),
         kotlinVersion = KotlinVersion.CURRENT,
         apiVersion = KotlinVersion.CURRENT,
         compilerVersion = KotlinVersion.CURRENT,
         codeGenerator = SimpleCodeGenerator(),
         logger = NoOpKSPLogger,
         platforms = listOf(DefaultJsPlatformInfo),
      )
      // Build a list where Spec1 lands at index 0 and Spec lands at index 10.
      // Without underscore: both would produce the alias "Spec10".
      val specs = mutableListOf(SimpleKSClassDeclaration("com.example.Spec1"))
      repeat(9) { i -> specs.add(SimpleKSClassDeclaration("com.example.Filler$i")) }
      specs.add(SimpleKSClassDeclaration("com.example.Spec"))

      val file = TestEngineGenerator(env).createFileSpec(specs, emptyList()).toString()

      file shouldContain """import com.example.Spec1 as Spec1_0"""
      file shouldContain """import com.example.Spec as Spec_10"""
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
            SimpleKSClassDeclaration("com.sksamuel.a.MyTestSpec",),
         ),
         configs = emptyList(),
      ).toString()

      file shouldContain """val config = null"""
   }

})

