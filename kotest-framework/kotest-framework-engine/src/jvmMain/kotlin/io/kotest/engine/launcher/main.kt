package io.kotest.engine.launcher

import io.kotest.core.descriptors.DescriptorPaths
import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.cli.parseArgs
import io.kotest.engine.extensions.ProvidedDescriptorFilter
import io.kotest.engine.launcher.LauncherArgs.ARG_CANDIDATES
import io.kotest.engine.launcher.LauncherArgs.ARG_LISTENER
import io.kotest.engine.launcher.LauncherArgs.DESCRIPTOR
import io.kotest.engine.launcher.LauncherArgs.REPORTER
import io.kotest.engine.launcher.LauncherArgs.SPEC
import io.kotest.engine.launcher.LauncherArgs.TESTPATH
import io.kotest.engine.launcher.LauncherArgs.WRITER
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.LoggingTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.runBlocking
import kotlin.reflect.KClass
import kotlin.system.exitProcess

object LauncherArgs {

   // required to pass the candidates to the engine
   const val ARG_CANDIDATES = "candidates"

   // these are optional

   // used to specify if we want team city or console output
   const val ARG_LISTENER = "--listener"

   // used to filter to a single spec or test within a spec
   const val DESCRIPTOR = "descriptor"

   // these are deprecated kotest 5 flags kept for backwards compatibility
   const val SPEC = "spec"
   const val TESTPATH = "testpath"
   const val REPORTER = "reporter"
   const val WRITER = "writer"
}

/**
 * The entry point for the launcher.
 *
 * Parses the cli args, creates [io.kotest.engine.listener.TestEngineListener]s and invokes
 * the test engine using a [TestEngineLauncher].
 *
 * --- IMPORTANT NOTE ---
 * This is used by the Gradle and Intellij plugins (and other third party clients).
 * Therefore, the package name and contract for this main method **MUST** remain backwards compatible.
 */
fun main(args: Array<String>) {

   println("Starting Kotest launcher with args: ${args.joinToString(";")}")

   val launcherArgs = parseArgs(args.toList())
   println("Parsed args: $launcherArgs")

   // The enigne *must* be given the classes to execute - in Kotest 6 the engine does not perform scanning
   // It is the responsibility of the caller to pass this information.
   // In Kotest 5 a similar argument was called --spec to specify a single class but kotest 6 uses --candidates
   // we must support both for backwards compatibility
   // todo do we need to do this? if people are upgrading to kotest 6 they can update the plugin too?
   val candidatesArg = launcherArgs[ARG_CANDIDATES]
      ?: launcherArgs[SPEC]
      ?: error("The $ARG_CANDIDATES arg must be provided")

   @Suppress("UNCHECKED_CAST")
   val classes = candidatesArg.split(';').map { Class.forName(it).kotlin as KClass<out Spec> }

   // we support --descriptor to support an exact descriptor path as a way to run a single test
   val descriptorFilter = buildDescriptorFilter(launcherArgs)

   // Kotest 5 supported --testpath and didn't support the a descriptor selector, only the test name
   // but we can combine that with the --spec arg which we know must be present in kotest 5 if testpath is
   // this exists so people can upgrade to kotest 6 but keep the old plugin
   // todo do we need to do this? if people are upgrading to kotest 6 they can update the plugin too?
   val descriptorFilterKotest5 = buildKotest5DescriptorFilter(launcherArgs)

   val outputListener = buildOutputTestEngineListener(launcherArgs)

   // we want to collect the results, so we can check if we need exit with an error
   val collector = CollectingTestEngineListener()

   val launcher = TestEngineLauncher(
      CompositeTestEngineListener(
         collector,
         LoggingTestEngineListener,// we use this to write to the kotest log file
         ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(outputListener))
      )
   ).withClasses(classes)
      .addExtensions(listOfNotNull(descriptorFilter, descriptorFilterKotest5))

   runBlocking {
      launcher.async()
   }

   // there could be threads in the background that will stop the launcher shutting down
   // for example if a test keeps a thread running,
   // so we must force the exit
   if (collector.errors) exitProcess(-1) else exitProcess(0)
}

private fun buildOutputTestEngineListener(launcherArgs: Map<String, String>): TestEngineListener {
   return TestEngineListenerBuilder.builder()
      .withType(launcherArgs[ARG_LISTENER] ?: launcherArgs[REPORTER] ?: launcherArgs[WRITER])
      .build()
}

private fun buildDescriptorFilter(launcherArgs: Map<String, String>): ProvidedDescriptorFilter? {
   return launcherArgs[DESCRIPTOR]?.let { descriptor ->
      println("Making a filter from input $descriptor")
      ProvidedDescriptorFilter(DescriptorPaths.parse(descriptor))
   }
}

private fun buildKotest5DescriptorFilter(launcherArgs: Map<String, String>): ProvidedDescriptorFilter? {
   return launcherArgs[TESTPATH]?.let { test ->
      launcherArgs[SPEC]?.let { spec ->
         ProvidedDescriptorFilter(DescriptorPaths.parse("$spec/$test"))
      }
   }
}
