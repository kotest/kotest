package io.kotest.plugin.intellij.linemarker

import com.intellij.execution.TestStateStorage
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo
import com.intellij.icons.AllIcons
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.gradle.GradleUtils
import org.jetbrains.kotlin.idea.base.util.module
import javax.swing.Icon

object LineMarkerUtils {

   // icons list https://jetbrains.design/intellij/resources/icons_list/
   val runIcon = AllIcons.RunConfigurations.TestState.Run
   val runRunIcon = AllIcons.RunConfigurations.TestState.Run_run
   val failedIcon = AllIcons.RunConfigurations.TestState.Red2
   val successIcon = AllIcons.RunConfigurations.TestState.Green2

   /**
    * Determines the spec status.
    *
    * The URL format varies depending on the Kotest version:
    *
    * 1. Kotest 6.1+ (with or without Kotest Gradle plugin): java:suite://fqn
    *
    */
   fun determineSpecStatus(element: LeafPsiElement, fqn: String): TestStatus {
      if(!GradleUtils.isKotest61OrAbove(element.module?.project)) return TestStatus.UNKNOWN
      val storage = TestStateStorage.getInstance(element.project)
      val allKeys = storage.keys

      val matchingKey = allKeys.find { key ->
         key == "java:suite://$fqn"
      }
      val state = matchingKey?.let { storage.getState(it) }

      return state?.let { getTestStatus(it.magnitude) } ?: TestStatus.UNKNOWN
   }

   /**
    * Determines the test status for individual tests.
    *
    * The URL format varies whether the Kotest Gradle plugin is present:
    *
    * 1. Kotest 6.1+ with Kotest Gradle plugin:
    *    java:test://<kotest>fqn/testName -- innerTestMaybe</kotest>TestDisplayName
    *    java:suite://<kotest>fqn/testName -- innerTestMaybe</kotest>TestDisplayName
    *
    * 2. Kotest 6.1+ without Kotest Gradle plugin:
    *    java:test://fqn/testDisplayName
    *    java:suite://fqn/testDisplayName
    *
    */
   fun determineTestStatus(element: LeafPsiElement, test: Test): TestStatus {
      if(!GradleUtils.isKotest61OrAbove(element.module?.project)) return TestStatus.UNKNOWN

      val storage = TestStateStorage.getInstance(element.project)
      val allKeys = storage.keys
      return determineTestStatus(test, allKeys, storage)
   }

   /**
    * Determines the test status by checking the [TestStateStorage] for matching keys.
    *
    * We first try to find a match using the kotest tags format, which is applicable if the user has the Kotest Gradle plugin applied.
    * If that fails, we fall back to the plain format, which is used when the plugin is not applied .
    * If both attempts fail, we return [TestStatus.UNKNOWN], which in turn returns the simple play gutter.
    *
    * Note: We check for Kotest 6.1+ to be present and return [TestStatus.UNKNOWN] if not, to avoid wasting time
    * looking up keys in the storage for older versions of Kotest that might not follow the same pattern.
    */
   private fun determineTestStatus(
      test: Test,
      allKeys: Collection<String>,
      storage: TestStateStorage
   ) =
      determineTestStatusWithKotestTags(test, allKeys, storage)
         ?: determineTestStatusWithoutKotestTags(test, allKeys, storage) ?: TestStatus.UNKNOWN


   /**
    * Determines test status using kotest tags format (Kotest 6.1+ with Kotest Gradle plugin).
    * Format: java:test://<kotest>fqn/testName -- innerTestMaybe</kotest>TestDisplayName
    */
   private fun determineTestStatusWithKotestTags(
      test: Test,
      allKeys: Collection<String>,
      storage: TestStateStorage
   ): TestStatus? {
      val kotestPathMarker = "<kotest>${test.descriptorPath()}</kotest>"

      val matchingKey = allKeys.find { key ->
         (key.startsWith("java:test://") || key.startsWith("java:suite://")) && key.contains(kotestPathMarker)
      }

      val state = matchingKey?.let { storage.getState(it) }
      return state?.let { getTestStatus(it.magnitude) }
   }

   /**
    * Determines test status using plain format (Kotest 6.1+ without Gradle plugin).
    * Format: java:test://fqn/testPath or java:suite://fqn/testPath
    */
   private fun determineTestStatusWithoutKotestTags(
      test: Test,
      allKeys: Collection<String>,
      storage: TestStateStorage
   ): TestStatus? {
      val pathMarkers = listOf(
         // Format: fqn/context -- test (for specs and containers)
         test.descriptorPath(),
         // Format: fqn/displayName  (for tests)
         "${test.specClassName.fqName?.asString()}/${test.name.displayName()}"
      )

      val state = pathMarkers.firstNotNullOfOrNull { pathMarker ->
         val matchingKey = allKeys.find { key ->
            (key.startsWith("java:test://") || key.startsWith("java:suite://")) &&
               (key == "java:test://$pathMarker" || key == "java:suite://$pathMarker" ||
                  key.startsWith("java:test://$pathMarker") || key.startsWith("java:suite://$pathMarker"))
         }
         matchingKey?.let { storage.getState(it) }
      }

      return state?.let { getTestStatus(it.magnitude) }

   }

   private fun getTestStatus(magnitude: Int): TestStatus {
      return when (magnitude) {
         TestStateInfo.Magnitude.FAILED_INDEX.value,
         TestStateInfo.Magnitude.ERROR_INDEX.value -> TestStatus.FAILED

         TestStateInfo.Magnitude.PASSED_INDEX.value,
         TestStateInfo.Magnitude.COMPLETE_INDEX.value -> TestStatus.PASSED

         else -> TestStatus.UNKNOWN
      }
   }

   fun determineIconFromStatus(
      testStatus: TestStatus,
      specIcon: Boolean = false
   ): Icon =
      when (testStatus) {
         TestStatus.FAILED -> failedIcon
         TestStatus.PASSED -> successIcon
         TestStatus.UNKNOWN -> runRunIcon.takeIf { specIcon } ?: runIcon
      }

   enum class TestStatus {
      PASSED, FAILED, UNKNOWN
   }
}



