package io.kotest.core.test

import io.kotest.core.config.Project
import io.kotest.core.engine.KotestFrameworkSystemProperties
import io.kotest.core.spec.focusTests
import io.kotest.core.spec.resolvedTags
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
 * - Excluded tags have been specified and this test has a [Tag] which is one of those excluded
 * - Included tags have been specified and this test either has no tags, or does not have a tag that is one of those included
 * - The test is filtered out via a [TestFilter]
 *
 * Note: tags are defined either through [TestCaseConfig] or in the [Spec] dsl.
 */
fun TestCase.isActive(): Boolean {

   // this sys property disables the use of !
   // when it's not set, then we use ! to disable tests
   val bangEnabled = sysprop(KotestFrameworkSystemProperties.disableBangPrefix) == null
   if (isBang() && bangEnabled) {
      log("${description.fullName()} is disabled by bang")
      return false
   }

   if (!config.enabled) {
      log("${description.fullName()} is disabled by enabled property in config")
      return false
   }

   if (!config.enabledIf(this)) {
      log("${description.fullName()} is disabled by enabledIf function in config")
      return false
   }

   // if we have tags specified on this
   val enabledInTags = Project.tags().isActive(config.tags + spec.resolvedTags())
   if (!enabledInTags) {
      log("${description.fullName()} is disabled by tags")
      return false
   }

   val includedByFilters = Project.testFilters().all { it.filter(this.description) }
   if (!includedByFilters) {
      log("${description.fullName()} is excluded by test case filters")
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
