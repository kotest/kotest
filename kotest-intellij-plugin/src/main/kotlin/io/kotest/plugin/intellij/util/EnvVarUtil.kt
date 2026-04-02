package io.kotest.plugin.intellij.util

import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import kotlin.collections.plus

object EnvVarUtil {
   const val KOTEST_TEST_ENABLED_OVERRIDE = "KOTEST_TEST_ENABLED_OVERRIDE"
   const val KOTEST_IDEA_PLUGIN = "KOTEST_IDEA_PLUGIN"
   const val KOTEST_TAGS = "KOTEST_TAGS"
   const val KOTEST_INVOCATION_COUNT = "KOTEST_INVOCATION_COUNT"

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
   fun removeKotestTags(settings : ExternalSystemTaskExecutionSettings) {
      with(settings.env) {
        remove(KOTEST_TAGS)
      }
   }

   fun setInvocationCount(settings : ExternalSystemTaskExecutionSettings, invocationCount: Int, resetAction: () -> Unit) {
      with(settings.env){
         put(KOTEST_INVOCATION_COUNT,invocationCount.toString())
      }
      resetAction()
   }
}
