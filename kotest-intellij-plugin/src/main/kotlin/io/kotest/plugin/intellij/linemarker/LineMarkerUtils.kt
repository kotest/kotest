package io.kotest.plugin.intellij.linemarker

import com.intellij.execution.TestStateStorage
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test

object LineMarkerUtils {

   /**
    * Determines the spec state.
    *
    * The URL format is expected to always be: java:suite://fqn
    */
   fun determineSpecState(element: LeafPsiElement, fqn: String): TestStateStorage.Record? {
      val storage = TestStateStorage.getInstance(element.project)
      val allKeys = storage.keys

      val matchingKey = allKeys.find { key ->
         key == "java:suite://$fqn"
      }
      return matchingKey?.let { storage.getState(it) }
   }

   /**
    * Determines the test status for individual tests.
    *
    * The URL format varies by whether the Kotest Gradle plugin is present and Kotest version,
    * generally we try to determine the test status by looking in the following urls (in order):
    *
    * 1a. java:test://<kotest>fqn/testName -- innerTestMaybe</kotest>TestDisplayName
    * 1b. java:suite://<kotest>fqn/testName -- innerTestMaybe</kotest>TestDisplayName
    * 2a. java:test://fqn/testDisplayName
    * 2b. java:suite://fqn/testDisplayName
    */
   fun determineTestState(element: LeafPsiElement, test: Test): TestStateStorage.Record? {
      val storage = TestStateStorage.getInstance(element.project)
      val allKeys = storage.keys
      return determineTestStatus(test, allKeys, storage)
   }

   /**
    * Determines the test state by checking the [TestStateStorage] for matching keys.
    *
    * We first try to find a match using the kotest tags format, which is applicable if the user has the Kotest Gradle plugin applied.
    * If that fails, we fall back to the plain format, which is used when the plugin is not applied .
    */
   private fun determineTestStatus(
      test: Test,
      allKeys: Collection<String>,
      storage: TestStateStorage
   ) =
      determineTestStatusWithKotestTags(test, allKeys, storage)
         ?: determineTestStatusWithoutKotestTags(test, allKeys, storage)


   /**
    * Determines test status using kotest tags format.
    * Format: java:test://<kotest>fqn/testName -- innerTestMaybe</kotest>TestDisplayName
    */
   private fun determineTestStatusWithKotestTags(
      test: Test,
      allKeys: Collection<String>,
      storage: TestStateStorage
   ): TestStateStorage.Record? {
      val kotestPathMarker = "<kotest>${test.descriptorPath()}</kotest>"

      val matchingKey = allKeys.find { key ->
         (key.startsWith("java:test://") || key.startsWith("java:suite://")) && key.contains(kotestPathMarker)
      }

      return matchingKey?.let { storage.getState(it) }
   }

   /**
    * Determines test status using plain format.
    * Format: java:test://fqn/testPath or java:suite://fqn/testPath
    */
   private fun determineTestStatusWithoutKotestTags(
      test: Test,
      allKeys: Collection<String>,
      storage: TestStateStorage
   ): TestStateStorage.Record? {
      val pathMarkers = listOf(
         // Format: fqn/context -- test (for specs and containers)
         test.descriptorPath(),
         // Format: fqn/displayName  (for tests)
         "${test.specClassName.fqName?.asString()}/${test.name.displayName()}"
      )

      return pathMarkers.firstNotNullOfOrNull { pathMarker ->
         val matchingKey = allKeys.find { key ->
            (key.startsWith("java:test://") || key.startsWith("java:suite://")) &&
               (key == "java:test://$pathMarker" || key == "java:suite://$pathMarker" ||
                  key.startsWith("java:test://$pathMarker") || key.startsWith("java:suite://$pathMarker"))
         }
         matchingKey?.let { storage.getState(it) }
      }
   }
}



