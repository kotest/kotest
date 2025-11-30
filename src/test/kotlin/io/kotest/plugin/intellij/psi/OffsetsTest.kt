package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.nio.file.Paths

class OffsetsTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testLineOffsets() {
      val psiFile = myFixture.configureByFile("/funspec.kt")
      val offsets = psiFile.offsetForLine(22)
      offsets.shouldNotBeNull()
      offsets.first shouldBe 359
      offsets.last shouldBe 388
   }

   fun testFindElementForAGivenLine() {
      val psiFile = myFixture.configureByFile("/funspec.kt")
      val element: PsiElement? = psiFile.elementAtLine(25)
      element.shouldNotBeNull()
      element.node.shouldBeInstanceOf<PsiElement>()
      element.startOffset shouldBe 404
      element.endOffset shouldBe 408
   }
}
