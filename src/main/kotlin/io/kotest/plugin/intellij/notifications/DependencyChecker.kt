package io.kotest.plugin.intellij.notifications

import com.intellij.ide.BrowserUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.notification.impl.NotificationFullContent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.testFramework.PsiTestUtil

object DependencyChecker {

   private val Group = NotificationGroup("Kotest", NotificationDisplayType.BALLOON, true)
   private val ConsoleDep = Dependency("io.kotest", "kotest-runner-console-jvm")
   private val RequiredDeps = listOf(ConsoleDep)

   private fun OrderEnumerator.libraries(): List<Dependency> {
      val libraries = mutableListOf<Dependency>()
      forEachLibrary { library ->
         val dep = library.name?.removePrefix("Gradle: ")?.removePrefix("Maven: ")
         if (dep != null) {
            val components = dep.split(':')
            if (components.size == 3) {
               val (group, artifact, _) = components
               libraries.add(Dependency(group, artifact))
            }
         }
         true
      }
      return libraries.toList()
   }

   private fun libraries(project: Project): List<Dependency> = OrderEnumerator.orderEntries(project).libraries()
   private fun libraries(module: Module): List<Dependency> = OrderEnumerator.orderEntries(module).libraries()

   private fun hasDependency(dep: Dependency, project: Project): Boolean = libraries(project).any { it == dep }
   private fun hasDependency(dep: Dependency, module: Module): Boolean = libraries(module).any { it == dep }

   fun checkMissingDependencies(project: Project): Boolean {
      RequiredDeps.forEach {
         if (!hasDependency(it, project)) {
            showDependencyNotification(it, project)
            return false
         }
      }
      return true
   }

   fun checkMissingDependencies(module: Module): Boolean {
      RequiredDeps.forEach {
         if (!hasDependency(it, module)) {
            showDependencyNotification(it, module.project)
            return false
         }
      }
      return true
   }

   private fun showDependencyNotification(dep: Dependency, project: Project) {

      val notification = object : Notification(
         Group.displayId,
         "Kotest",
         "Add <b>${dep.asString()}</b> to your build to execute tests using the kotest plugin. Required version is 4.1.0 or higher.",
         NotificationType.ERROR
      ), NotificationFullContent {}


      val openMavenAction = object : AnAction("Open maven central") {
         override fun actionPerformed(e: AnActionEvent) {
            BrowserUtil.browse("https://search.maven.org/search?q=g:${dep.group}%20AND%20a:${dep.artifact}")
         }
      }

      notification.addAction(openMavenAction)

      Notifications.Bus.notify(notification, project)
   }
}

data class Dependency(val group: String, val artifact: String) {
   fun asString(): String = "${group}:${artifact}"
}
