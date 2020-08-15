package io.kotest.core.config

@Deprecated("Replaced with io.kotest.core.configuration. This alias will be removed in 4.3")
val Project = configuration

///**
// * A central store of project wide configuration. This configuration contains defaults for kotest, and is
// * supplemented by user configuration (if present) as loaded by [detectConfig].
// *
// * Additionally config can be programatically added to this class by using the mutator methods such
// * as [registerExtension] or [setFailOnIgnoredTests].
// */
//@OptIn(ExperimentalTime::class)
//object Project {
//
//   private val userconf = detectConfig()
//
//   private var autoScanIgnoredClasses: List<KClass<*>> = emptyList()
//    */
//   fun tags(): Tags {
//      val tags = tagExtensions().map { it.tags() }
//      return if (tags.isEmpty()) Tags.Empty else tags.reduce { a, b -> a.combine(b) }
//   }
//
//
//   fun autoScanIgnoredClasses() = autoScanIgnoredClasses
//
//   fun setAutoScanIgnoredClasses(classes: List<KClass<*>>) {
//      autoScanIgnoredClasses = classes
//   }
//}
