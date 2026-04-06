package io.kotest.plugin.intellij.psi

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.styles.DescribeSpecStyle
import io.kotest.plugin.intellij.styles.FunSpecStyle
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

/**
 * Tests for [KtClassOrObject.kotestStyleSyntactic].
 *
 * All checks are purely syntactic — no type resolution is performed.  Classes that sit
 * behind intermediate abstract base specs (single- and multi-level) must be recognised
 * transitively.  Circular-inheritance hierarchies must not cause a stack overflow.
 */
class KotestStyleSyntacticTest : LightJavaCodeInsightFixtureTestCase() {

   private fun ktClass(code: String, name: String): KtClassOrObject {
      val file = myFixture.configureByText("Spec.kt", code) as KtFile
      return file.declarations.filterIsInstance<KtClassOrObject>().first { it.name == name }
   }

   // -------------------------------------------------------------------------
   // Direct supertype
   // -------------------------------------------------------------------------

   fun `test direct FunSpec supertype returns FunSpecStyle`() {
      val cls = ktClass("class MySpec : FunSpec()", "MySpec")
      cls.kotestStyleSyntactic() shouldBe FunSpecStyle
   }

   fun `test direct DescribeSpec supertype returns DescribeSpecStyle`() {
      val cls = ktClass("class MySpec : DescribeSpec()", "MySpec")
      cls.kotestStyleSyntactic() shouldBe DescribeSpecStyle
   }

   // -------------------------------------------------------------------------
   // Non-spec classes
   // -------------------------------------------------------------------------

   fun `test plain class with no supertype returns null`() {
      val cls = ktClass("class Foo", "Foo")
      cls.kotestStyleSyntactic() shouldBe null
   }

   fun `test class with unrelated supertype returns null`() {
      val cls = ktClass("class MyClass : Runnable", "MyClass")
      cls.kotestStyleSyntactic() shouldBe null
   }

   // -------------------------------------------------------------------------
   // Recursive (transitive) inheritance
   // -------------------------------------------------------------------------

   fun `test two-level inheritance recognises spec style`() {
      val code = """
         abstract class BaseSpec : FunSpec()
         class ConcreteSpec : BaseSpec()
      """.trimIndent()
      val cls = ktClass(code, "ConcreteSpec")
      cls.kotestStyleSyntactic() shouldBe FunSpecStyle
   }

   fun `test three-level inheritance recognises spec style`() {
      val code = """
         abstract class Level1 : FunSpec()
         abstract class Level2 : Level1()
         class Level3 : Level2()
      """.trimIndent()
      val cls = ktClass(code, "Level3")
      cls.kotestStyleSyntactic() shouldBe FunSpecStyle
   }

   fun `test intermediate class itself is also recognised`() {
      val code = """
         abstract class BaseSpec : FunSpec()
         class ConcreteSpec : BaseSpec()
      """.trimIndent()
      val cls = ktClass(code, "BaseSpec")
      cls.kotestStyleSyntactic() shouldBe FunSpecStyle
   }

   fun `test different spec style through intermediate class`() {
      val code = """
         abstract class BaseSpec : DescribeSpec()
         class ConcreteSpec : BaseSpec()
      """.trimIndent()
      val cls = ktClass(code, "ConcreteSpec")
      cls.kotestStyleSyntactic() shouldBe DescribeSpecStyle
   }

   // -------------------------------------------------------------------------
   // Safety: circular inheritance
   // -------------------------------------------------------------------------

   fun `test circular inheritance does not throw`() {
      // Semantically invalid but syntactically parseable; must not cause a StackOverflowError.
      val code = """
         class A : B()
         class B : A()
      """.trimIndent()
      val cls = ktClass(code, "A")
      cls.kotestStyleSyntactic() shouldBe null
   }
}
