package io.kotest.plugin.intellij.tests

import com.intellij.codeInsight.CodeInsightUtil
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.PostprocessReformattingAspect
import com.intellij.refactoring.util.classMembers.MemberInfo
import com.intellij.testIntegration.createTest.CreateTestDialog
import com.intellij.testIntegration.createTest.JavaTestGenerator
import com.intellij.util.concurrency.annotations.RequiresReadLock
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.KotestTestFramework
import io.kotest.plugin.intellij.styles.FunSpecStyle
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisFromWriteAction
import org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.permissions.allowAnalysisFromWriteAction
import org.jetbrains.kotlin.analysis.api.permissions.allowAnalysisOnEdt
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.refactoring.memberInfo.toKotlinMemberInfo
import org.jetbrains.kotlin.name.FqName
import java.util.Properties

/**
 * Used to create "Template" test class files.
 */
class KotestTestGenerator : JavaTestGenerator() {

   override fun toString(): String = KotlinLanguage.INSTANCE.displayName

   override fun generateTest(project: Project, d: CreateTestDialog): PsiElement? {
      // I do not currently know how to limit the test generator to only kotest, therefore we can check
      // the framework name and delegate to the JavaTestGenerator one. But this only works while JavaTestGenerator
      // is public, and has a zero arg constructor, so I would like to find a better long term solution:
      // https://intellij-support.jetbrains.com/hc/en-us/community/posts/15040678418450-How-to-choose-when-to-apply-TestGenerator
      if (d.selectedTestFrameworkDescriptor.name != Constants.FRAMEWORK_NAME)
         return super.generateTest(project, d)
      return PostprocessReformattingAspect.getInstance(project).postponeFormattingInside(Computable {
         ApplicationManager.getApplication().runWriteAction(Computable<PsiElement?> {
            val file = generateTestFile(project, d)
            if (file != null) {
               // without this the file is created but the caret stays in the original file
               CodeInsightUtil.positionCursor(project, file, file)
            }
            file
         })
      })
   }

   private fun styleForSuperClass(fqn: FqName): SpecStyle =
      SpecStyle.Companion.styles.find { it.fqn() == fqn } ?: FunSpecStyle

   private fun generateTestFile(project: Project, d: CreateTestDialog): PsiFile? {

      val result = when (val framework = d.selectedTestFrameworkDescriptor) {
         is KotestTestFramework -> {
            IdeDocumentHistory.getInstance(project).includeCurrentPlaceAsChangePlace()

            val fileTemplate = FileTemplateManager.getInstance(project).getCodeTemplate("kotest_class.kt")
            val defaultProperties = FileTemplateManager.getInstance(project).defaultProperties
            val props = Properties(defaultProperties)
            props.setProperty(FileTemplate.ATTRIBUTE_NAME, d.className)

            val targetClass = d.targetClass
            if (targetClass != null && targetClass.isValid) {
               props.setProperty(FileTemplate.ATTRIBUTE_CLASS_NAME, targetClass.qualifiedName)
            }

            val superClass = d.superClassName ?: framework.defaultSuperClass
            val simpleName = superClass.split('.').last()
            props.setProperty("SUPERCLASS_FQ", superClass)
            props.setProperty("SUPERCLASS", simpleName)

            if (d.shouldGeneratedBefore()) {
               props.setProperty("BEFORE_TEST", "true")
            }

            if (d.shouldGeneratedAfter()) {
               props.setProperty("AFTER_TEST", "true")
            }

            if (d.selectedMethods.isNotEmpty()) {
               val style = styleForSuperClass(FqName(superClass))
               val tests = testNames(d.selectedMethods).map { methodName ->
                  style.generateTest(targetClass.name ?: "SpecName", methodName)
               }
               val testBodies = tests.joinToString("\n\n")
               props.setProperty("TEST_METHODS", testBodies)
            }

            FileTemplateUtil.createFromTemplate(fileTemplate, d.className, props, d.targetDirectory)
         }
         else -> null
      }
      return when (result) {
         is PsiFile -> result
         else -> null
      }
   }

   @OptIn(
      KaAllowAnalysisOnEdt::class,
      KaAllowAnalysisFromWriteAction::class,
   )
   @RequiresReadLock
   private fun testNames(selectedMethods: Collection<MemberInfo>): List<String> {
      return allowAnalysisFromWriteAction {
         allowAnalysisOnEdt {
            selectedMethods.mapNotNull {
               it.toKotlinMemberInfo()?.member?.name
            }
         }
      }
   }
}
