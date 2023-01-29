package io.kotest.plugin.intellij.inspections

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath

class ShouldBeInstanceOfQuickFix(element: KtBinaryExpression) : LocalQuickFixOnPsiElement(element) {

   override fun getFamilyName(): String = "Replace with shouldBeInstanceOf"
   override fun getText(): String = "Replace with shouldBeInstanceOf"

   override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
      val psiFactory = KtPsiFactory(project)
      val left = (startElement as KtBinaryExpression).left as KtParenthesizedExpression
      val isexpr = left.expression as KtIsExpression
      val newExpression = psiFactory.createExpression("${isexpr.leftHandSide.text}.shouldBeInstanceOf<${isexpr.typeReference?.typeElement?.text}>()")
      startElement.replace(newExpression)

      if (file is KtFile) {
         if (file.importDirectives.none { it.importedFqName?.asString() == "io.kotest.matchers.types.shouldBeInstanceOf" }) {
            val importList = file.importList
            val import = psiFactory.createImportDirective(
               ImportPath(
                  FqName("io.kotest.matchers.types.shouldBeInstanceOf"),
                  false,
                  null
               )
            )
            importList?.add(psiFactory.createNewLine())
            importList?.add(import)
         }
      }
   }
}
