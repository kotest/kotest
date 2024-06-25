package io.kotest.plugin.intellij.psi

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
      val offsets = psiFile.offsetForLine(21)
      offsets.shouldNotBeNull()
      offsets.first shouldBe 324
      offsets.last shouldBe 353
   }

   fun testFindElementForAGivenLine() {
      val psiFile = myFixture.configureByFile("/funspec.kt")
      val element = psiFile.elementAtLine(24)
      element.shouldNotBeNull()
//      element.node.shouldBeInstanceOf<PsiWhiteSpace>()
      element.startOffset shouldBe 361
      element.endOffset shouldBe 369
   }
}
