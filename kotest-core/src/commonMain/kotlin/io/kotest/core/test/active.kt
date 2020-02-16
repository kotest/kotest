package io.kotest.core.test

import io.kotest.core.config.Project
import io.kotest.core.filters.TestFilterResult
import io.kotest.core.spec.focusTests
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
 *
 * Note2: Focused tests will override any settings here.
 *
 */
fun TestCase.isActive(): Boolean {
   val focused = isFocused() && isTopLevel()
   val hasFocused = spec.focusTests().isNotEmpty()
   val enabledInConfig = config.enabled && config.enabledIf(this)
   val bangEnabled = sysprop("kotest.bang.disable") == null
   val disabledViaBang = isBang() && bangEnabled
   val activeViaTags = Project.tags().isActive(config.tags + spec.tags() + spec._tags)
   val filtered = Project.testCaseFilters()
      .map { it.filter(description) }.any { it == TestFilterResult.Exclude }
   return focused || !hasFocused && enabledInConfig && !disabledViaBang && !filtered && activeViaTags
}
