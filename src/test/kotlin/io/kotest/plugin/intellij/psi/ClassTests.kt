package io.kotest.plugin.intellij.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class ClassTests : LightCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testEnclosingClass() {
      val psiFile = myFixture.configureByFile("/funspec.kt")
      val element = psiFile.elementAtLine(21)
      element.shouldNotBeNull()
      val ktclass = element.enclosingClass()
      ktclass.shouldNotBeNull()
      ktclass.name shouldBe "FunSpecExampleTest"
   }

   fun testGetSuperClass() {
      val psiFile = myFixture.configureByFile("/funspec.kt")
      val superclass = psiFile.elementAtLine(21)?.enclosingClass()?.getSuperClassSimpleName()
      superclass.shouldNotBeNull()
      superclass shouldBe "FunSpec"
   }

   fun testEnclosingClassOrObjectForClassOrObjectToken() {
      val psiFile = myFixture.configureByFile("/funspec.kt")
      val element = psiFile.findElementAt(229) as LeafPsiElement
      val ktclass = element.enclosingClassOrObjectForClassOrObjectToken()
      ktclass.shouldNotBeNull()
      ktclass.name shouldBe "FunSpecExampleTest"
   }
}
