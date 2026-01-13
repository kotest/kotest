package io.kotest.plugin.intellij

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.testIntegration.createTest.TestGenerators
import io.kotest.plugin.intellij.tests.KotestTestGenerator
import org.jetbrains.kotlin.idea.KotlinLanguage

class KotestProjectActivity : ProjectActivity {

   private val logger = logger<KotestProjectActivity>()

   override suspend fun execute(project: Project) {
      // for some reason registering via plugin.xml does not work for this test extension
      // see https://youtrack.jetbrains.com/issue/IJPL-184822/Registering-a-testGenerator-via-the-plugin-XML-does-not-work
      TestGenerators.INSTANCE.addExplicitExtension(KotlinLanguage.INSTANCE, KotestTestGenerator())
      ImplicitUsageProvider.EP_NAME.extensionsIfPointIsRegistered.forEach { logger.info("ImplicitUsageProvider=$it") }
   }
}
