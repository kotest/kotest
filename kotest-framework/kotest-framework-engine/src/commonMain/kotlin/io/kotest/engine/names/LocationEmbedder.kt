package io.kotest.engine.names

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor

/**
 * Historically, IntelliJ jump-to-source for nested Kotest tests was driven by injecting a
 * `<kotest>path/to/test</kotest>` prefix into the JUnit displayName, which the Kotest plugin
 * stripped on the IDE side. Users without the plugin saw the raw mangled name.
 *
 * Newer plugins navigate via the `MethodSource` already attached to each leaf descriptor:
 * `(className=<spec fqn>, methodName=<segment>/<segment>/...)`. IntelliJ's JUnit5 launcher
 * surfaces this as `proxy.locationUrl = "java:test://<fqn>/<segment>/..."` and the plugin
 * parses that directly, so the displayName no longer needs to carry a tag.
 *
 * This object is retained only so older callers continue to compile. It is no longer used by
 * the JUnit Platform engine and may be removed in a future major release.
 */
@KotestInternal
@Deprecated(
   "Navigation now flows via the MethodSource attached to each test descriptor (proxy.locationUrl). " +
      "The displayName tag is no longer emitted by the engine and this helper is unused. " +
      "It will be removed in a future major release.",
   level = DeprecationLevel.WARNING,
)
object LocationEmbedder {

   const val OPEN_TAG = "<kotest>"
   const val CLOSE_TAG = "</kotest>"

   fun embeddedTestName(descriptor: Descriptor, formattedName: String): String {
      return OPEN_TAG + descriptor.path().value + CLOSE_TAG + formattedName
   }
}
