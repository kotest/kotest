package io.kotlintest.plugin.intellij

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
import com.intellij.testIntegration.createTest.CreateTestDialog
import com.intellij.testIntegration.createTest.TestGenerator
import io.kotlintest.plugin.intellij.styles.SpecStyle
import io.kotlintest.plugin.intellij.styles.StringSpecStyle
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.refactoring.memberInfo.toKotlinMemberInfo
import org.jetbrains.kotlin.name.FqName
import java.util.*

class KotlinTestTestGenerator : TestGenerator {

  override fun toString(): String = KotlinLanguage.INSTANCE.displayName

  override fun generateTest(project: Project, d: CreateTestDialog): PsiElement? {
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
      SpecStyle.specs.find { it.fqn() == fqn } ?: StringSpecStyle

  private fun generateTestFile(project: Project, d: CreateTestDialog): PsiFile? {

    val framework = d.selectedTestFrameworkDescriptor
    val result = when (framework) {
      is KotlinTestTestFramework -> {
        IdeDocumentHistory.getInstance(project).includeCurrentPlaceAsChangePlace()

        val fileTemplate = FileTemplateManager.getInstance(project).getCodeTemplate("KotlinTest Class")
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
          val tests = d.selectedMethods.mapNotNull {
            it.toKotlinMemberInfo()?.member?.name
          }.map { methodName ->
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
}