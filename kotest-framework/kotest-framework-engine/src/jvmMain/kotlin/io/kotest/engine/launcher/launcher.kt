package io.kotest.engine.launcher

//import io.kotest.framework.discovery.Discovery
//import io.kotest.framework.discovery.DiscoveryRequest
//import io.kotest.framework.discovery.DiscoveryResult
//import io.kotest.framework.discovery.DiscoverySelector
import io.kotest.engine.tags.TagExpression
import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.TestEngineListener
import kotlin.reflect.KClass

/**
 * Creates a [TestEngineLauncher] to be used to launch the test engine.
 */
@Suppress("UNCHECKED_CAST")
internal fun setupLauncher(
   args: LauncherArgs,
   listener: TestEngineListener,
): Result<TestEngineLauncher> = runCatching {

   val specClass = args.spec?.let { (Class.forName(it) as Class<Spec>).kotlin }

   // todo move discovery out of this module entirely
//   val (specs, _, error) = specs(specClass, args.packageName)
   val specs = emptyList<KClass<out Spec>>()
   val error: Throwable? = null
   val filter = if (args.testpath == null || specClass == null) null else {
      TestPathTestCaseFilter(args.testpath, specClass)
   }

   if (error != null) throw error

   TestEngineLauncher(listener)
      .withExtensions(listOfNotNull(filter))
      .withTagExpression(args.tagExpression?.let { TagExpression(it) })
      .withClasses(specs)
}

///**
// * Returns the spec classes to execute by using an FQN class name, a package scan,
// * or a full scan.
// */
//private fun specs(specClass: KClass<out Spec>?, packageName: String?): DiscoveryResult {
//   // if the spec class was null, then we perform discovery to locate all the classes
//   // otherwise that specific spec class is used
//   return when (specClass) {
//      null -> scan(packageName)
//      else -> DiscoveryResult(listOf(specClass), emptyList(), null)
//   }
//}
//
//private fun scan(packageName: String?): DiscoveryResult {
//   val packageSelector = packageName?.let { DiscoverySelector.PackageDiscoverySelector(it) }
//   val req = DiscoveryRequest(selectors = listOfNotNull(packageSelector))
//   val discovery = Discovery(ProjectConfiguration())
//   return discovery.discover(req)
//}
