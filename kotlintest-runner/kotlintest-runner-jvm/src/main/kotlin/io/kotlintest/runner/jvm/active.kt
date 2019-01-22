package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.TestCase
import io.kotlintest.TestFilterResult
import io.kotlintest.TestCaseFilter
import io.kotlintest.Tag
import io.kotlintest.Spec
import io.kotlintest.TestCaseConfig

/**
 * Returns true if the given [TestCase] is active.
 *
 * A test can be active or inactive.
 *
 * A test is inactive if
 *
 * - The `enabled` property is set to false in the [TestCaseConfig] associated with the test.
 * - The name of the test is prefixed with "!" and System.getProperty("kotlintest.bang.disable") has a null value (ie, not defined)
 * - Exclude tag filters have been specified and this test has a [Tag] which is one of those excluded
 * - Include tag filters have been specified and this test either has no tags, or does not have a tag that is one of those included
 * - The test is filtered out via a [TestCaseFilter]
 *
 * Note: tags are defined either through [TestCaseConfig] or in the [Spec] itself.
 *
 */
fun isActive(test: TestCase): Boolean {
  val enabledInConfig = test.config.enabled
  val disabledViaBang = test.name.startsWith("!") && System.getProperty("kotlintest.bang.disable") == null
  val allTags = test.config.tags + test.spec.tags()
  val activeViaTags = Project.tags().isActive(allTags)
  val filtered = Project.testCaseFilters().map { it.filter(test.description) }.any { it == TestFilterResult.Exclude }
  return enabledInConfig && activeViaTags && !disabledViaBang && !filtered
}