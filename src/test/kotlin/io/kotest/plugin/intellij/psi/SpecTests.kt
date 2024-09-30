package io.kotest.plugin.intellij.psi

import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class SpecTests : LightJavaCodeInsightFixtureTestCase() {

   override fun getProjectDescriptor(): LightProjectDescriptor = JAVA_11

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testasKtClassOrObjectOrNull() {

      val psiFile = myFixture.configureByFiles(
         "/io/kotest/plugin/intellij/subclasses.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      (psiFile[0].findElementAt(626) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec1"
      (psiFile[0].findElementAt(657) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec2"
      (psiFile[0].findElementAt(693) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec3"
      (psiFile[0].findElementAt(717) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec4"
      (psiFile[0].findElementAt(743) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec5"
      (psiFile[0].findElementAt(773) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec6"
      (psiFile[0].findElementAt(803) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec7"
      (psiFile[0].findElementAt(826) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec8"
      (psiFile[0].findElementAt(852) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec9"
      (psiFile[0].findElementAt(879) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec10"
      (psiFile[0].findElementAt(909) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec11"
      (psiFile[0].findElementAt(947) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString() shouldBe "io.kotest.plugin.intellij.Spec12"
      (psiFile[0].findElementAt(975) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString().shouldBeNull()
      (psiFile[0].findElementAt(1007) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString().shouldBeNull()
      (psiFile[0].findElementAt(1035) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString().shouldBeNull()
      (psiFile[0].findElementAt(1060) as LeafPsiElement).asKtClassOrObjectOrNull()
         ?.takeIfRunnableSpec()?.fqName?.asString().shouldBeNull()
   }
}
