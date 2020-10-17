package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class SpecTests : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testIsContainedInSpec() {
      val psiFile1 = myFixture.configureByFile("/funspec.kt")
      psiFile1.elementAtLine(21)!!.isContainedInSpec() shouldBe true
      val psiFile2 = myFixture.configureByFile("/stringspec.kt")
      psiFile2.elementAtLine(11)!!.isContainedInSpec() shouldBe true
      val psiFile3 = myFixture.configureByFile("/freespec.kt")
      psiFile3.elementAtLine(18)!!.isContainedInSpec() shouldBe true
   }

   fun testIsSubclassOfSpec() {
      val psiFile = myFixture.configureByFile("/specs/issubclass.kt")
      val element1 = psiFile.findElementAt(255) as PsiElement
      element1.enclosingKtClass()?.name shouldBe "IsSubclassOfSpec1"
      element1.enclosingKtClass()?.isSpec() shouldBe true

      val element2 = psiFile.findElementAt(350) as PsiElement
      element2.enclosingKtClass()?.name shouldBe "IsSubclassOfSpec2"
      element2.enclosingKtClass()?.isSpec() shouldBe true

      val element3 = psiFile.findElementAt(400) as PsiElement
      element3.enclosingKtClass()?.name shouldBe "IsSubclassOfSpec3"
      element3.enclosingKtClass()?.isSpec() shouldBe false

      val element4 = psiFile.findElementAt(450) as PsiElement
      element4.enclosingKtClass()?.name shouldBe "IsSubclassOfSpec4"
      element4.enclosingKtClass()?.isSpec() shouldBe false
   }
}
