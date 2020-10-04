package io.kotest.plugin.intellij.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.name.FqName
import java.nio.file.Paths

class ClassTests : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testEnclosingClass() {
      val psiFile = myFixture.configureByFile("/funspec.kt")
      val element = psiFile.elementAtLine(21)
      element.shouldNotBeNull()
      val ktclass = element.enclosingKtClass()
      ktclass.shouldNotBeNull()
      ktclass.name shouldBe "FunSpecExampleTest"
   }

   fun testSuperClassSimpleName() {
      val psiFile = myFixture.configureByFile("/funspec.kt")
      val superclass = psiFile.elementAtLine(21)?.enclosingKtClass()?.getSuperClassSimpleName()
      superclass.shouldNotBeNull()
      superclass shouldBe "FunSpec"
   }

   fun testEnclosingClassOrObjectForClassOrObjectToken() {
      val psiFile = myFixture.configureByFile("/funspec.kt")
      val element = psiFile.findElementAt(229) as LeafPsiElement
      val ktclass = element.ktclassIfCanonicalSpecLeaf()
      ktclass.shouldNotBeNull()
      ktclass.name shouldBe "FunSpecExampleTest"
   }

   fun testIsSubclass() {
      val psiFile = myFixture.configureByFile("/classes/issubclass.kt")
      val element = psiFile.findElementAt(255) as LeafPsiElement
      element.enclosingKtClass()?.isSubclass(FqName("io.foo")) shouldBe false
      element.enclosingKtClass()?.isSubclass(FqName("io.kotest.core.spec.style.FunSpec")) shouldBe true
      element.enclosingKtClass()?.isSubclass(FqName("io.kotest.core.spec.style.StringSpec")) shouldBe false
   }

   fun testClasses() {
      val psiFile = myFixture.configureByFile("/classes/childktclasses.kt")
      psiFile.classes().map { it.name } shouldBe listOf(
         "Child1",
         "Child2",
         "Child3",
         "Child4",
         "Child5",
         "Child6",
         "Child7",
         "Child8",
         "Child9",
         "Child10",
         "Child11",
         "Child12"
      )
   }
}
