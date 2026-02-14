package io.kotest.plugin.intellij.linemarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.execution.TestStateStorage
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.styles.SpecStyle

/**
 * A line marker that adds a success or failure icon next to the tests that have run.
 * This works by checking the test state storage for keys that match the test path.
 *
 * @since Kotest 6.1.x
 */
class FailedTestLineMarker : LineMarkerProvider {
   // icons list https://jetbrains.design/intellij/resources/icons_list/
   private val failedCase = AllIcons.RunConfigurations.TestFailed to "Test failed"
   private val successCase = AllIcons.RunConfigurations.TestPassed to "Test passed"

   private val possibleLeafElements = SpecStyle.styles.flatMap { it.possibleLeafElements() }

   override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
      val testDescriptorPath = LineMarkerUtils.validateElementAndReturnTestDescriptorPath(
         element = element,
         possibleLeafElements = possibleLeafElements,
         requiredKotest61OrAbove = true
      ) ?: return null

      val storage = TestStateStorage.getInstance(element.project)

      /**
       * We store the test URLs in the format:
       * <kotest>fqn/testName -- innerTestMaybe</kotest>TestDisplayName
       * We need to search for keys that contain our test path, which is the part inside the kotest tags
       */
      val kotestPathMarker = "<kotest>$testDescriptorPath</kotest>"

      val allKeys = storage.keys
      val matchingKey = allKeys.find { key ->
         key.startsWith("java:test://") && key.contains(kotestPathMarker)
      } ?: return null

      val state = storage.getState(matchingKey) ?: return null
      return when (state.magnitude) {
         TestStateInfo.Magnitude.FAILED_INDEX.value,
         TestStateInfo.Magnitude.ERROR_INDEX.value -> {
            MainEditorLineMarkerInfo(
               element,
               failedCase.second,
               failedCase.first
            )
         }

         TestStateInfo.Magnitude.PASSED_INDEX.value,
         TestStateInfo.Magnitude.COMPLETE_INDEX.value -> {
            MainEditorLineMarkerInfo(
               element,
               successCase.second,
               successCase.first
            )
         }

         else -> null
      }
   }
}
