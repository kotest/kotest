package io.kotest.plugin.intellij.tests

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.testIntegration.createTest.TestGenerators
import org.jetbrains.kotlin.idea.KotlinLanguage

class KotestStartupActivity : StartupActivity {
   override fun runActivity(project: Project) {
      // for some reason registering via plugin.xml does not work for this test extension
      // see https://youtrack.jetbrains.com/issue/IJPL-184822/Registering-a-testGenerator-via-the-plugin-XML-does-not-work
      TestGenerators.INSTANCE.addExplicitExtension(KotlinLanguage.INSTANCE, KotestTestGenerator())
   }
}
