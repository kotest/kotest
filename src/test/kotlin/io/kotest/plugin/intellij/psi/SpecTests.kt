package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.nio.file.Paths

class SpecTests : LightCodeInsightFixtureTestCase() {

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

   fun testCallbacks() {
      val psiFile = myFixture.configureByFile("/callbacks.kt")
      val ktclass = psiFile.specs()[0]
      val callbacks = ktclass.callbacks()

      callbacks[0].psi.startOffset shouldBe 115
      callbacks[0].type shouldBe CallbackType.BeforeTest

      callbacks[1].psi.startOffset shouldBe 138
      callbacks[1].type shouldBe CallbackType.AfterTest

      callbacks[2].psi.startOffset shouldBe 160
      callbacks[2].type shouldBe CallbackType.BeforeSpec

      callbacks[3].psi.startOffset shouldBe 183
      callbacks[3].type shouldBe CallbackType.AfterSpec
   }

   fun testIsSubclassOfSpec() {
      val psiFile = myFixture.configureByFile("/specs/issubclass.kt")
      val element1 = psiFile.findElementAt(255) as PsiElement
      element1.enclosingKtClass()?.name shouldBe "IsSubclassOfSpec1"
      element1.enclosingKtClass()?.isSubclassOfSpec() shouldBe true

      val element2 = psiFile.findElementAt(350) as PsiElement
      element2.enclosingKtClass()?.name shouldBe "IsSubclassOfSpec2"
      element2.enclosingKtClass()?.isSubclassOfSpec() shouldBe true

      val element3 = psiFile.findElementAt(400) as PsiElement
      element3.enclosingKtClass()?.name shouldBe "IsSubclassOfSpec3"
      element3.enclosingKtClass()?.isSubclassOfSpec() shouldBe false

      val element4 = psiFile.findElementAt(450) as PsiElement
      element4.enclosingKtClass()?.name shouldBe "IsSubclassOfSpec4"
      element4.enclosingKtClass()?.isSubclassOfSpec() shouldBe false
   }
}
