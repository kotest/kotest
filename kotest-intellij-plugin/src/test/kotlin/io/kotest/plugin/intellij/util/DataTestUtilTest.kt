package io.kotest.plugin.intellij.util

import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.testMode
import org.jetbrains.kotlin.psi.KtCallExpression
import java.nio.file.Paths

class DataTestUtilTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testDataTestTagMaybeReturnsTagExpressionForNestedDataTests() {
      testMode = true

      myFixture.configureByFiles(
         "/datatestspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val allDataTestCalls = findAllCallExpressionsByNames(setOf("withData", "withTests", "withContexts"))

      allDataTestCalls.size shouldBe 13

      // Data test inside "child context" at line 13 (withData)
      val firstChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 13 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfChildContext).apply {
         this.shouldNotBeNull()
         tag shouldBe "(kotest.data.13) | !kotest.data"
         ancestorTestPath shouldBe "parent context -- child context"
      }

      // Data test inside "child context" at line 16 (withTests)
      val secondChildOfChildContext = allDataTestCalls.find { getLineNumber(it) == 16 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(isDataTest = true, secondChildOfChildContext).apply {
         this.shouldNotBeNull()
         tag shouldBe "(kotest.data.16) | !kotest.data"
         ancestorTestPath shouldBe "parent context -- child context"
      }

      // Data test inside "parent context" at line 20 (withContexts)
      val firstChildOfParentContext = allDataTestCalls.find { getLineNumber(it) == 20 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(isDataTest = true, firstChildOfParentContext).apply {
         this.shouldNotBeNull()
         tag shouldBe "(kotest.data.20) | !kotest.data"
         ancestorTestPath shouldBe "parent context"
      }

      // Root withData at line 26
      val rootWithData = allDataTestCalls.find { getLineNumber(it) == 26 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(isDataTest = true, rootWithData)?.tag shouldBe "kotest.data.26"

      // firstChild (withTests) at line 27 - exclude siblings at lines 30 and 48
      val firstChild = allDataTestCalls.find { getLineNumber(it) == 27 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(
         isDataTest = true,
         firstChild
      )?.tag shouldBe "kotest.data.26 & !kotest.data.30 & !kotest.data.48"

      // secondChild (withData) at line 30 - exclude siblings at lines 27 and 48
      val secondChild = allDataTestCalls.find { getLineNumber(it) == 30 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(
         isDataTest = true,
         secondChild
      )?.tag shouldBe "kotest.data.26 & !kotest.data.27 & !kotest.data.48"

      // firstChildOfSecondChild (withContexts) at line 31 - exclude siblings at lines 39 and 42, plus parent's siblings
      val firstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 31 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(
         isDataTest = true,
         firstChildOfSecondChild
      )?.tag shouldBe "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.39 & !kotest.data.42"

      // firstChildOfFirstChildOfSecondChild (withTests) at line 32 - exclude sibling at line 35
      val firstChildOfFirstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 32 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(
         isDataTest = true,
         firstChildOfFirstChildOfSecondChild
      )?.tag shouldBe "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.39 & !kotest.data.42 & !kotest.data.35"

      // secondChildOfFirstChildOfSecondChild (withTests) at line 35 - exclude sibling at line 32
      val secondChildOfFirstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 35 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(
         isDataTest = true,
         secondChildOfFirstChildOfSecondChild
      )?.tag shouldBe "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.39 & !kotest.data.42 & !kotest.data.32"

      // secondChildOfSecondChild (withTests) at line 39 - exclude siblings at lines 31 and 42
      val secondChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 39 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(
         isDataTest = true,
         secondChildOfSecondChild
      )?.tag shouldBe "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.31 & !kotest.data.42"

      // thirdChildOfSecondChild (withData) at line 42 - exclude siblings at lines 31 and 39
      val thirdChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 42 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(
         isDataTest = true,
         thirdChildOfSecondChild
      )?.tag shouldBe "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.31 & !kotest.data.39"

      // firstAndOnlyChildOfThirdChildOfSecondChild (withTests) at line 43 - same as parent (no siblings)
      val firstAndOnlyChild = allDataTestCalls.find { getLineNumber(it) == 43 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(
         isDataTest = true,
         firstAndOnlyChild
      )?.tag shouldBe "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.31 & !kotest.data.39"

      // thirdChild (withTests) at line 48 - exclude siblings at lines 27 and 30
      val thirdChild = allDataTestCalls.find { getLineNumber(it) == 48 }.shouldNotBeNull()
      DataTestUtil.dataTestInfoMaybe(
         isDataTest = true,
         thirdChild
      )?.tag shouldBe "kotest.data.26 & !kotest.data.27 & !kotest.data.30"
   }

   private fun findAllCallExpressionsByNames(names: Set<String>): List<KtCallExpression> {
      val psiFile = myFixture.file
      return PsiTreeUtil.findChildrenOfType(psiFile, KtCallExpression::class.java)
         .filter { it.calleeExpression?.text in names }
   }

   private fun getLineNumber(psi: com.intellij.psi.PsiElement): Int {
      val file = psi.containingFile ?: return -1
      val document = file.viewProvider.document ?: return -1
      return document.getLineNumber(psi.textOffset) + 1
   }
}
