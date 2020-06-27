package io.kotest.core.test

import io.kotest.core.config.Project
import io.kotest.core.filters.TestFilterResult
import io.kotest.core.spec.focusTests
import io.kotest.mpp.log
import io.kotest.mpp.sysprop

/**
 * Returns true if the given [TestCase] is active.
 *
 * A test can be active or inactive.
 *
 * A test is inactive if
 *
 * - The `enabled` property is set to false in the [TestCaseConfig] associated with the test.
 * - The name of the test is prefixed with "!" and System.getProperty("kotest.bang.disable") has a null value (ie, not defined)
 * - Exclude tag filters have been specified and this test has a [Tag] which is one of those excluded
 * - Include tag filters have been specified and this test either has no tags, or does not have a tag that is one of those included
 * - The test is filtered out via a [TestCaseFilter]
 *
 * Note: tags are defined either through [TestCaseConfig] or in the [Spec] dsl.
 */
fun TestCase.isActive(): Boolean {

   // this sys property disables the use of !
   // when it's not set, then we use ! to disable tests
   val bangEnabled = sysprop("kotest.bang.disable") == null
   if (isBang() && bangEnabled) {
      log("${description.fullName()} is disabled by bang")
      return false
   }

   if (!config.enabled) return false
   if (!config.enabledIf(this)) return false

   // if we have tags specified on this
   val enabledInTags = Project.tags().isActive(config.tags + spec.tags() + spec._tags)
   if (!enabledInTags) {
      log("${description.fullName()} is disabled by tags")
      return false
   }

   val filterResults = Project.testCaseFilters().map { it to it.filter(description) }
   if (filterResults.any { it.second == TestFilterResult.Exclude }) {
      log("${description.fullName()} is excluded by test case filters [${filterResults}]")
      return false
   }

   // if the spec has focused tests, and this test is top level and not focused, then it's not active
   val specHasFocusedTests = spec.focusTests().isNotEmpty()
   if (isTopLevel() && !isFocused() && specHasFocusedTests) {
      log("${description.fullName()} is disabled by another test having focus")
      return false
   }

   return true
}
