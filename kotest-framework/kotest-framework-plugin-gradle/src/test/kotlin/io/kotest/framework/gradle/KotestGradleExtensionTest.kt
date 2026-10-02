package io.kotest.framework.gradle

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.gradle.testfixtures.ProjectBuilder

class KotestGradleExtensionTest : FunSpec({

   // see https://github.com/kotest/kotest/issues/6231
   test("kotestVersion should be the version of the plugin and should not be changeable") {
      val project = ProjectBuilder.builder().build()
      project.pluginManager.apply(KotestPlugin::class.java)
      val kotest = project.extensions.getByType(KotestGradleExtension::class.java)

      kotest.kotestVersion.get() shouldBe System.getProperty("kotestVersion")
      shouldThrow<IllegalStateException> { kotest.kotestVersion.set("1.2.3") }
   }
})
