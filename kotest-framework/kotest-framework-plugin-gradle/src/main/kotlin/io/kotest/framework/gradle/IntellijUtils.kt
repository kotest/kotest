package io.kotest.framework.gradle

internal object IntellijUtils {
   private const val IDEA_PROP = "idea.active"

   // this system property is added by intellij itself when running tasks
   fun isIntellij() = System.getProperty(IDEA_PROP) != null
}
