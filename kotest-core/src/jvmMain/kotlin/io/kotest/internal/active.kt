package io.kotest.internal

import io.kotest.Project
import io.kotest.Spec
import io.kotest.Tag
import io.kotest.TestCase
import io.kotest.core.TestFilterResult

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
 * Note: tags are defined either through [TestCaseConfig] or in the [Spec] itself.
 *
 * Note2: Focused tests will override any settings here.
 *
 */
fun isActive(test: TestCase): Boolean {
  val focused = test.isFocused() && test.isTopLevel()
  val hasFocused = test.spec.hasFocusedTest()
  val enabledInConfig = test.config.enabled
  val disabledViaBang = test.name.startsWith("!") && System.getProperty("kotest.bang.disable") == null
  val activeViaTags = Project.tags().isActive(test.config.tags + test.spec.tags())
  val filtered = Project.testCaseFilters().map { it.filter(test.description) }.any { it == TestFilterResult.Exclude }
  return focused || !hasFocused && enabledInConfig && activeViaTags && !disabledViaBang && !filtered
}
