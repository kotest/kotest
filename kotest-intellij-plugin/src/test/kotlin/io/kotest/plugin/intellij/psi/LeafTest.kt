package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty

class LeafTest : LightJavaCodeInsightFixtureTestCase() {

   fun testObjectShouldBeKtClassOrObject() {
      val element = PsiFileFactory
         .getInstance(project)
         .createFileFromText("test.kt", KotlinFileType.INSTANCE, """object Foo {}""")
         .findElementAt(2)!! as LeafPsiElement
      element.context.shouldBeInstanceOf<KtObjectDeclaration>()
      element.asKtClassOrObjectOrNull().shouldBeInstanceOf<KtClassOrObject>()
   }

   fun testClassShouldBeKtClassOrObject() {
      val element = PsiFileFactory
         .getInstance(project)
         .createFileFromText("test.kt", KotlinFileType.INSTANCE, """class Foo {}""")
         .findElementAt(2)!! as LeafPsiElement
      element.context.shouldBeInstanceOf<KtClass>()
      element.asKtClassOrObjectOrNull().shouldBeInstanceOf<KtClassOrObject>()
   }

   fun testFunctionShouldNotBeKtClassOrObject() {
      val element = PsiFileFactory
         .getInstance(project)
         .createFileFromText("test.kt", KotlinFileType.INSTANCE, """fun foo() {}""")
         .findElementAt(2)!! as LeafPsiElement
      element.context.shouldBeInstanceOf<KtFunction>()
      element.asKtClassOrObjectOrNull() shouldBe null
   }

   fun testStringShouldNotBeKtClassOrObject() {
      val element = PsiFileFactory
         .getInstance(project)
         .createFileFromText("test.kt", KotlinFileType.INSTANCE, """val name = "sam"""")
         .findElementAt(6)!! as LeafPsiElement
      element.context.shouldBeInstanceOf<KtProperty>()
      element.asKtClassOrObjectOrNull() shouldBe null
   }
}
