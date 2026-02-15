package io.kotest.plugin.intellij.linemarker

import com.intellij.execution.TestStateStorage
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo
import com.intellij.icons.AllIcons
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import javax.swing.Icon

object LineMarkerUtils {

   // icons list https://jetbrains.design/intellij/resources/icons_list/
   val runIcon = AllIcons.RunConfigurations.TestState.Run
   val runRunIcon = AllIcons.RunConfigurations.TestState.Run_run
   val failedIcon = AllIcons.RunConfigurations.TestState.Red2
   val successIcon = AllIcons.RunConfigurations.TestState.Green2

   fun determineTestStatus(element: LeafPsiElement, test: Test): TestStatus {
      /**
       * We store the test URLs in the format:
       * java:test://<kotest>fqn/testName -- innerTestMaybe</kotest>TestDisplayName - for leaf tests
       * java:suite://<kotest>fqn/testName -- innerTestMaybe</kotest>TestDisplayName - for container tests
       * We need to search for keys that contain our test path, which is the part inside the kotest tags
       */
      val storage = TestStateStorage.getInstance(element.project)
      val kotestPathMarker = "<kotest>${test.descriptorPath()}</kotest>"
      val allKeys = storage.keys
      val state = allKeys.find { key ->
         (key.startsWith("java:test://") || key.startsWith("java:suite://")) && key.contains(kotestPathMarker)
      }?.let { matchingKey -> storage.getState(matchingKey) } ?: return TestStatus.UNKNOWN
      return getTestStatus(state.magnitude)
   }

   fun determineSpecStatus(element: LeafPsiElement, fqn: String): TestStatus {
      /**
       * We store the spec URLs in the format:
       * java:suite://fqn
       * We can search for keys that match this format and contain our spec's FQN
       */
      val storage = TestStateStorage.getInstance(element.project)
      val allKeys = storage.keys
      val state = allKeys.find { key ->
         key == "java:suite://$fqn"
      }?.let { matchingKey -> storage.getState(matchingKey) } ?: return TestStatus.UNKNOWN
      return getTestStatus(state.magnitude)
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



