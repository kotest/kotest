package io.kotest.plugin.intellij.locations

import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsAdapter
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import io.kotest.plugin.intellij.psi.kotestStyleSyntactic
import org.jetbrains.kotlin.idea.stubindex.KotlinFullClassNameIndex
import org.jetbrains.plugins.gradle.execution.test.runner.GradleSMTestProxy

/**
 * Listens to SMTest events and installs an [EmbeddedLocationTestLocator] on each Kotest
 * [SMTestProxy] so that nested tests can be navigated via "jump to source" inside IntelliJ.
 *
 * Two strategies are supported in priority order:
 *
 *  1. **`locationUrl` strategy** (preferred, no displayName mangling). The IDEA JUnit5 launcher
 *     converts a [org.junit.platform.engine.support.descriptor.MethodSource] of the form
 *     `(className=fqn, methodName=seg/seg/...)` into a `java:test://<fqn>/<seg>/<seg>` URL on
 *     [SMTestProxy.getLocationUrl]. We detect those URLs and install our locator without touching
 *     the displayName. This is what Kotest 6.x (and later) emits. A single-segment methodName
 *     (no `/`) can't be told apart from a plain JVM method (i.e. `@Test`) by shape alone, so we resolve
 *     the FQN to confirm it's actually a Kotest spec before taking over navigation from it -
 *     Kotest's own single-level (non-nested) tests aren't real JVM methods, so IntelliJ's default
 *     locator can't resolve them and "Jump to Source" silently does nothing for them otherwise.
 *
 *  2. **Legacy displayName tag strategy**. Older Kotest engines wrapped the path in a
 *     `<kotest>fqn/test -- nested</kotest>` prefix on the proxy display name. We still strip the
 *     tag and install the locator so users on the new plugin see clean names and working
 *     navigation against older engine versions.
 */
internal class EmbeddedLocationSMTRunnerEventsAdapter(private val project: Project) : SMTRunnerEventsAdapter() {

   override fun onSuiteStarted(suite: SMTestProxy) {
      handleKotestLocator(suite)
   }

   override fun onTestStarted(test: SMTestProxy) {
      handleKotestLocator(test)
   }

   override fun onTestIgnored(test: SMTestProxy) {
      handleKotestLocator(test)
   }

   private fun handleKotestLocator(proxy: SMTestProxy) {
      // Strategy 1: legacy <kotest>...</kotest> tag in the display name.
      // Checked first so we strip the tag for users running new plugin against old engines.
      val legacy = EmbeddedLocationParser.parse(proxy.name)
      if (legacy != null) {
         proxy.locator = EmbeddedLocationTestLocator(legacy)
         proxy.setPresentableName(legacy.presentableName)
         return
      }

      // Only Gradle-run proxies carry a parentId - the IntelliJ-native JUnit launcher path uses
      // plain SMTestProxy, so this is empty there and parseLocationUrl falls back to the
      // single-segment behaviour it already has.
      val ancestorNames = GradleParentIdParser.ancestorContextNames((proxy as? GradleSMTestProxy)?.parentId)

      // Strategy 2: java:test://<fqn>/<segment>/<segment> URL produced from a JUnit MethodSource
      val fromUrl = EmbeddedLocationParser.parseLocationUrl(proxy.locationUrl, proxy.name, ancestorNames, ::isKotestSpec)
      if (fromUrl != null) {
         proxy.locator = EmbeddedLocationTestLocator(fromUrl)
         return
      }

      if (isJavaSuiteClass(proxy)) {
         // if we have a java:suite locator for a top level class, this doesn't work for kotlin native, so we can
         // use our own locator which will work for both kmp and jvm
         proxy.locator = MultiplatformJavaSuiteLocator()
      }
   }

   // resolves whether the given FQN is a Kotest spec class, so a single-segment methodName (which
   // can't be distinguished from a plain JVM @Test method by shape alone) is only handed to our
   // locator when it genuinely belongs to Kotest. Fails closed (false) while indexing, leaving the
   // default locator in place rather than risking a stub-index query on a half-built index.
   private fun isKotestSpec(fqn: String): Boolean {
      if (DumbService.isDumb(project)) return false
      return ReadAction.compute<Boolean, Throwable> {
         KotlinFullClassNameIndex[fqn, project, GlobalSearchScope.allScope(project)]
            .any { it.kotestStyleSyntactic() != null }
      }
   }

   // returns true if a class not a test
   internal fun isJavaSuiteClass(proxy: SMTestProxy): Boolean =
      proxy.locationUrl?.matches("java:suite://[a-zA-Z_.]+".toRegex()) == true
}

internal object EmbeddedLocationParser {

   private val tagRegex = "<kotest>(.*)</kotest>(.*)".toRegex()

   // FQN must start with a letter/underscore, then letters/digits/_/./$ — never `/`.
   private val fqnRegex = Regex("[A-Za-z_][A-Za-z0-9_.\$]*")

   fun parse(name: String): EmbeddedLocation? {
      val result = tagRegex.find(name) ?: return null
      return EmbeddedLocation(result.groupValues[1], result.groupValues[2])
   }

   /**
    * Parses a `java:test://<fqn>/<seg>/<seg>...` (or `java:suite://`) URL into an
    * [EmbeddedLocation] in the `fqn/seg -- seg` format expected by [EmbeddedLocationTestLocator].
    *
    * Returns null if the URL is not in this form, the FQN doesn't look like a Kotlin/Java FQN,
    * or the method-name component contains no `/` (single-method URL) and [isKotestSpec] says the
    * FQN isn't actually a Kotest spec - in which case it's a real JVM method and we should leave
    * the default locator alone.
    *
    * [ancestorNames], when non-empty, are prepended ahead of the URL's own segment(s) - this is
    * how a Gradle-run test's dropped ancestor containers (see [GradleParentIdParser]) get restored
    * into the path, rather than leaving [EmbeddedLocationTestLocator] to guess via a whole-tree
    * name search.
    */
   fun parseLocationUrl(
      locationUrl: String?,
      displayName: String,
      ancestorNames: List<String> = emptyList(),
      isKotestSpec: (String) -> Boolean,
   ): EmbeddedLocation? {
      if (locationUrl == null) return null
      val rest = when {
         locationUrl.startsWith("java:test://") -> locationUrl.removePrefix("java:test://")
         locationUrl.startsWith("java:suite://") -> locationUrl.removePrefix("java:suite://")
         else -> return null
      }
      val firstSlash = rest.indexOf('/')
      if (firstSlash <= 0) return null
      val fqn = rest.substring(0, firstSlash)
      if (!fqn.matches(fqnRegex)) return null
      val segments = rest.substring(firstSlash + 1)
      // No `/` in segments means it is a single-method URL, which is ambiguous by shape alone: it
      // could be a top-level (non-nested) Kotest test, or a real JVM method on a non-Kotest class.
      // Only bail to the default locator when the FQN genuinely isn't a Kotest spec - Kotest's
      // own single-level tests aren't real JVM methods, so the default locator can't resolve them.
      if (!segments.contains('/') && !isKotestSpec(fqn)) return null
      val allSegments = if (segments.contains('/')) segments.split("/") else ancestorNames + segments
      val path = "$fqn${DescriptorPaths.SPEC_DELIMITER}${allSegments.joinToString(DescriptorPaths.TEST_DELIMITER)}"
      return EmbeddedLocation(path, displayName)
   }
}

internal data class EmbeddedLocation(val path: String, val presentableName: String)

/**
 * Parses the debug/string representation of a Gradle `TestOperationDescriptor` id chain, as
 * exposed by `GradleSMTestProxy.getParentId()`.
 *
 * IntelliJ's Gradle test event integration surfaces only a leaf test's own name on `locationUrl`
 * (see [EmbeddedLocationSMTRunnerEventsAdapter]), dropping every ancestor container - but that
 * ancestry is still present in this id string, e.g.:
 *
 *   [id] > [Test suite 'parent'] > [Test suite 'grandparent'] > [Test class com.example.Spec] > [Task :mod:test]
 *
 * `SMTestProxy.getParent()` is not a reliable substitute for this - it can
 * skip intermediate levels that Gradle chose not to materialise as their own tree nodes - so this
 * parses the id string directly instead of walking the proxy tree.
 */
internal object GradleParentIdParser {

   private val suiteRegex = Regex("""\[Test suite '(.*?)']""")

   /**
    * Returns the ancestor container names, ordered from the outermost container down to the
    * immediate parent of the node this id belongs to (i.e. root-first, ready to have the node's
    * own name appended last). Returns an empty list if [parentId] is null or has no "Test suite"
    * entries (e.g. a top-level test/container directly under the spec).
    */
   fun ancestorContextNames(parentId: String?): List<String> {
      if (parentId == null) return emptyList()
      return suiteRegex.findAll(parentId).map { it.groupValues[1] }.toList().asReversed()
   }
}
