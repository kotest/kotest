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

      allDataTestCalls.size shouldBe 10

      // Root withData at line 10
      val rootWithData = allDataTestCalls.find { getLineNumber(it) == 10 }
      rootWithData.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, rootWithData) shouldBe "kotest.data.10"

      // firstChild (withTests) at line 11 - exclude siblings at lines 14 and 32
      val firstChild = allDataTestCalls.find { getLineNumber(it) == 11 }
      firstChild.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, firstChild) shouldBe "kotest.data.10 & !kotest.data.14 & !kotest.data.32"

      // secondChild (withData) at line 14 - exclude siblings at lines 11 and 32
      val secondChild = allDataTestCalls.find { getLineNumber(it) == 14 }
      secondChild.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, secondChild) shouldBe "kotest.data.10 & !kotest.data.11 & !kotest.data.32"

      // firstChildOfSecondChild (withContexts) at line 15 - exclude siblings at lines 23 and 26, plus parent's siblings
      val firstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 15 }
      firstChildOfSecondChild.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, firstChildOfSecondChild) shouldBe "kotest.data.10 & !kotest.data.11 & !kotest.data.32 & !kotest.data.23 & !kotest.data.26"

      // firstChildOfFirstChildOfSecondChild (withTests) at line 16 - exclude sibling at line 19
      val firstChildOfFirstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 16 }
      firstChildOfFirstChildOfSecondChild.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, firstChildOfFirstChildOfSecondChild) shouldBe "kotest.data.10 & !kotest.data.11 & !kotest.data.32 & !kotest.data.23 & !kotest.data.26 & !kotest.data.19"

      // secondChildOfFirstChildOfSecondChild (withTests) at line 19 - exclude sibling at line 16
      val secondChildOfFirstChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 19 }
      secondChildOfFirstChildOfSecondChild.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, secondChildOfFirstChildOfSecondChild) shouldBe "kotest.data.10 & !kotest.data.11 & !kotest.data.32 & !kotest.data.23 & !kotest.data.26 & !kotest.data.16"

      // secondChildOfSecondChild (withTests) at line 23 - exclude siblings at lines 15 and 26
      val secondChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 23 }
      secondChildOfSecondChild.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, secondChildOfSecondChild) shouldBe "kotest.data.10 & !kotest.data.11 & !kotest.data.32 & !kotest.data.15 & !kotest.data.26"

      // thirdChildOfSecondChild (withData) at line 26 - exclude siblings at lines 15 and 23
      val thirdChildOfSecondChild = allDataTestCalls.find { getLineNumber(it) == 26 }
      thirdChildOfSecondChild.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, thirdChildOfSecondChild) shouldBe "kotest.data.10 & !kotest.data.11 & !kotest.data.32 & !kotest.data.15 & !kotest.data.23"

      // firstAndOnlyChildOfThirdChildOfSecondChild (withTests) at line 27 - same as parent (no siblings)
      val firstAndOnlyChild = allDataTestCalls.find { getLineNumber(it) == 27 }
      firstAndOnlyChild.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, firstAndOnlyChild) shouldBe "kotest.data.10 & !kotest.data.11 & !kotest.data.32 & !kotest.data.15 & !kotest.data.23"

      // thirdChild (withTests) at line 32 - exclude siblings at lines 11 and 14
      val thirdChild = allDataTestCalls.find { getLineNumber(it) == 32 }
      thirdChild.shouldNotBeNull()
      DataTestUtil.dataTestTagMaybe(isDataTest = true, thirdChild) shouldBe "kotest.data.10 & !kotest.data.11 & !kotest.data.14"
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
