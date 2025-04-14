package io.kotest.framework.multiplatform.gradle

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.provider.ProviderFactory

@EnabledIf(LinuxOnlyGithubCondition::class)
class KotestMultiplatformCompilerGradlePluginTest : BehaviorSpec({

   Given("KotestMultiplatformCompilerGradlePlugin") {

      val kotestPlugin = kotestMultiplatformCompilerGradlePluginInstance()

      When("plugin is applied to a project") {
         val projectMock: Project = mockk {
            every { extensions } returns mockk {
               every { create<KotestPluginExtension>("kotestMultiplatform", any()) } returns mockk(relaxed = true)
            }
         }

         Then("expect it creates KotestPluginExtension") {
            kotestPlugin.apply(projectMock)

            verify(exactly = 1) { projectMock.extensions }
         }
      }
   }
})


private fun kotestMultiplatformCompilerGradlePluginInstance(
   providerFactoryMock: ProviderFactory = mockk(),
) = object : KotestMultiplatformCompilerGradlePlugin(
   providerFactoryMock,
) {}
