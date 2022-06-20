package io.kotest.plugin.intellij.ui

import com.intellij.execution.ExecutionBundle
import com.intellij.execution.configuration.BrowseModuleValueActionListener
import com.intellij.ide.util.PackageChooserDialog
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class PackageChooserActionListener<T : JComponent>(project: Project) : BrowseModuleValueActionListener<T>(project) {
   override fun showDialog(): String? {
      val dialog = PackageChooserDialog(
         ExecutionBundle.message("choose.package.dialog.title"),
         project
      )
      dialog.show()

      return dialog.selectedPackage?.qualifiedName
   }
}
