@file:Suppress("DEPRECATION")

package io.kotest.plugin.intellij.util

import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import io.kotest.plugin.intellij.run.idea.KotestRunConfiguration
import kotlin.collections.plus

object EnvVarUtil {
   const val KOTEST_TEST_ENABLED_OVERRIDE = "KOTEST_TEST_ENABLED_OVERRIDE"
   const val KOTEST_IDEA_PLUGIN = "KOTEST_IDEA_PLUGIN"
   const val KOTEST_TAGS = "KOTEST_TAGS"
   const val KOTEST_INVOCATION_COUNT = "KOTEST_INVOCATION_COUNT"
   const val KOTEST_DATA_TEST_ANCESTOR_PATH = "KOTEST_DATA_TEST_ANCESTOR_PATH"

   fun setKotestTestEnabledOverride(settings : ExternalSystemTaskExecutionSettings) {
      settings.env = settings.env + mapOf(KOTEST_TEST_ENABLED_OVERRIDE to "true")
   }

   fun setKotestIdeaPlugin(settings : ExternalSystemTaskExecutionSettings) {
      settings.env = settings.env + mapOf(KOTEST_IDEA_PLUGIN to "true")
   }

   fun setKotestTags(settings : ExternalSystemTaskExecutionSettings, tag : String) {
      with(settings.env){
         remove(KOTEST_TEST_ENABLED_OVERRIDE)
         put(KOTEST_TAGS, tag)
      }
   }

   fun setKotestTags(kotestRunConfiguration : KotestRunConfiguration, tag: String) {
      with(kotestRunConfiguration.envs){
         remove(KOTEST_TEST_ENABLED_OVERRIDE)
         put(KOTEST_TAGS, tag)
      }
   }

   fun removeKotestTags(settings : ExternalSystemTaskExecutionSettings) {
      with(settings.env) {
        remove(KOTEST_TAGS)
      }
   }

   fun removeKotestTags(kotestRunConfiguration : KotestRunConfiguration) {
      with(kotestRunConfiguration.envs){
         remove(KOTEST_TAGS)
      }
   }

   fun setKotestDataTestAncestorPath(settings: ExternalSystemTaskExecutionSettings, ancestorPath: String) {
      settings.env = settings.env + mapOf(KOTEST_DATA_TEST_ANCESTOR_PATH to ancestorPath)
   }

   fun setKotestDataTestAncestorPath(kotestRunConfiguration: KotestRunConfiguration, ancestorPath: String) {
      kotestRunConfiguration.envs[KOTEST_DATA_TEST_ANCESTOR_PATH] = ancestorPath
   }

   fun removeKotestDataTestAncestorPath(settings: ExternalSystemTaskExecutionSettings) {
      settings.env.remove(KOTEST_DATA_TEST_ANCESTOR_PATH)
   }

   fun removeKotestDataTestAncestorPath(kotestRunConfiguration: KotestRunConfiguration) {
      kotestRunConfiguration.envs.remove(KOTEST_DATA_TEST_ANCESTOR_PATH)
   }

   fun setInvocationCount(settings : ExternalSystemTaskExecutionSettings, invocationCount: Int, resetAction: () -> Unit) {
      with(settings.env){
         put(KOTEST_INVOCATION_COUNT,invocationCount.toString())
      }
      resetAction()
   }

   fun setInvocationCount(kotestRunConfiguration : KotestRunConfiguration, invocationCount: Int, resetAction: () -> Unit) {
      with(kotestRunConfiguration.envs){
         put(KOTEST_INVOCATION_COUNT,invocationCount.toString())
      }
      resetAction()
   }
}
