package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition

class TestExplorerCondition : Condition<Project> {
   override fun value(t: Project?): Boolean {
      return true
   }
}
