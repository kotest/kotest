package io.kotest.core

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
 * Note: tags are defined either through [TestCaseConfig] or in the [SpecConfiguration] dsl.
 *
 * Note2: Focused tests will override any settings here.
 *
 */
fun isActive(test: TestCase): Boolean {
  val focused = test.isFocused() && test.isTopLevel()
  val hasFocused = test.spec.focused().isNotEmpty()
  val enabledInConfig = test.config.enabled
  val disabledViaBang = test.name.startsWith("!") && sysprop("kotest.bang.disable").isEmpty()
  // todo val activeViaTags = Project.tags().isActive(test.config.tags + test.spec.tags())
  // todo val filtered = Project.testCaseFilters().map { it.filter(test.description) }.any { it == TestFilterResult.Exclude }
  return focused || !hasFocused && enabledInConfig && !disabledViaBang // && !filtered && activeViaTags
}
