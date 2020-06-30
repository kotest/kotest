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
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope

object DependencyChecker {

   private val Group = NotificationGroup("Kotest", NotificationDisplayType.BALLOON, true)

   private val ConsoleDep = Dependency(
      "io.kotest",
      "kotest-runner-console-jvm",
      "4.1.0",
      "io.kotest.runner.console.TeamCityConsoleWriter"
   )

   private val ReflectDep = Dependency("org.jetbrains.kotlin",
      "kotlin-reflect",
      "1.3.72",
      "kotlin.reflect.jvm.internal.ReflectProperties")

   private val RequiredDeps = listOf(ConsoleDep, ReflectDep)

   private fun hasDependency(dep: Dependency, module: Module): Boolean {
      val facade = JavaPsiFacade.getInstance(module.project)
      return facade.findClass(dep.fqn, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true)) != null
   }

   fun hasRequiredDependencies(module: Module, showNotification: Boolean): Boolean {
      RequiredDeps.forEach {
         if (!hasDependency(it, module)) {
            if (showNotification) {
               showDependencyNotification(it, module.project)
            }
            return false
         }
      }
      return true
   }

   private fun showDependencyNotification(dep: Dependency, project: Project) {

      val notification = object : Notification(
         Group.displayId,
         "Kotest",
         "Add <b>${dep.asString()}</b> to your build to execute tests using the kotest plugin. Required version is ${dep.version} or higher.",
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

data class Dependency(val group: String, val artifact: String, val version: String, val fqn: String) {
   fun asString(): String = "${group}:${artifact}"
}
