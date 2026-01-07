package io.kotest.plugin.intellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiDocumentManager
import io.kotest.plugin.intellij.psi.enclosingSpec
import io.kotest.plugin.intellij.psi.specStyle
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestElement
import org.jetbrains.kotlin.idea.refactoring.hostEditor

enum class Direction {
   Previous, Next
}

abstract class NavigateToTestAction(private val direction: Direction) : AnAction() {
   override fun actionPerformed(e: AnActionEvent) {

      val project = e.project ?: return
      val editor = e.dataContext.hostEditor ?: return
      val offset = editor.caretModel.offset

      val file = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return
      val element = file.findElementAt(offset) ?: return

      // gets the spec that the element is located in
      val spec = element.enclosingSpec() ?: return
      val style = spec.specStyle() ?: return

      // gets the test that contains the element where the action occurred.
      val test = style.findAssociatedTest(element) ?: return
      val alltests = style.tests(spec, false)

      fun flatten(tests:List<TestElement>): List<Test> {
         return tests.flatMap { listOf(it.test) + flatten(it.nestedTests) }
      }

      val tests = flatten(alltests)

      val index = tests.indexOfFirst { it.testPath() == test.testPath() }
      val target = when (direction) {
         Direction.Previous -> tests.getOrNull(index - 1)
         Direction.Next -> tests.getOrNull(index + 1)
      } ?: return

      if (target.psi is NavigatablePsiElement) {
         target.psi.navigate(true)
      }
   }
}

class NextTestAction : NavigateToTestAction(Direction.Next)
class PreviousTestAction : NavigateToTestAction(Direction.Previous)
