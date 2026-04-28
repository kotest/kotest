package io.kotest.plugin.intellij.locations

import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsAdapter
import com.intellij.execution.testframework.sm.runner.SMTestProxy

/**
 * Listens to SMTest events and installs an [EmbeddedLocationTestLocator] on each Kotest
 * [SMTestProxy] so that nested tests can be navigated via "jump to source" inside IntelliJ.
 *
 * Two strategies are supported in priority order:
 *
 *  1. **`locationUrl` strategy** (preferred, no displayName mangling). The IDEA JUnit5 launcher
 *     converts a [org.junit.platform.engine.support.descriptor.MethodSource] of the form
 *     `(className=fqn, methodName=seg/seg/...)` into a `java:test://<fqn>/<seg>/<seg>` URL on
 *     [SMTestProxy.getLocationUrl]. We detect those URLs (whose method-name component contains a
 *     `/`, which is impossible for a real JVM method) and install our locator without touching
 *     the displayName. This is what Kotest 6.x (and later) emits.
 *
 *  2. **Legacy displayName tag strategy**. Older Kotest engines wrapped the path in a
 *     `<kotest>fqn/test -- nested</kotest>` prefix on the proxy display name. We still strip the
 *     tag and install the locator so users on the new plugin see clean names and working
 *     navigation against older engine versions.
 */
internal class EmbeddedLocationSMTRunnerEventsAdapter : SMTRunnerEventsAdapter() {

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

      // Strategy 2: java:test://<fqn>/<segment>/<segment> URL produced from a Kotest MethodSource
      val fromUrl = EmbeddedLocationParser.parseLocationUrl(proxy.locationUrl, proxy.name)
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
    * or the method-name component contains no `/` (in which case it is most likely a real JVM
    * method on a non-Kotest class and we should leave the default locator alone).
    */
   fun parseLocationUrl(locationUrl: String?, displayName: String): EmbeddedLocation? {
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
      // No `/` in segments means it is a single-method URL. Real JVM methods cannot contain `/`,
      // so the only way to reach here is a top-level Kotest test or a regular JUnit method —
      // in both cases the default locator handles it acceptably.
      if (!segments.contains('/')) return null
      val path = "$fqn${DescriptorPaths.SPEC_DELIMITER}${segments.replace("/", DescriptorPaths.TEST_DELIMITER)}"
      return EmbeddedLocation(path, displayName)
   }
}

internal data class EmbeddedLocation(val path: String, val presentableName: String)
