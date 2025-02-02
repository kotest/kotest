package io.kotest.engine.launcher

import io.kotest.core.descriptors.DescriptorPaths
import io.kotest.core.spec.Spec
import io.kotest.engine.cli.parseArgs
import io.kotest.engine.extensions.ProvidedDescriptorFilter
import io.kotest.engine.launcher.LauncherArgs.CANDIDATES
import io.kotest.engine.launcher.LauncherArgs.DESCRIPTOR
import io.kotest.engine.launcher.LauncherArgs.LISTENER
import io.kotest.engine.launcher.LauncherArgs.REPORTER
import io.kotest.engine.launcher.LauncherArgs.SPEC
import io.kotest.engine.launcher.LauncherArgs.TERMCOLORS
import io.kotest.engine.launcher.LauncherArgs.TESTPATH
import io.kotest.engine.launcher.LauncherArgs.WRITER
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.LoggingTestEngineListener
import io.kotest.engine.listener.PinnedSpecTestEngineListener
import io.kotest.engine.listener.ThreadSafeTestEngineListener
import io.kotest.engine.runBlocking
import kotlin.reflect.KClass
import kotlin.system.exitProcess

object LauncherArgs {

   const val CANDIDATES = "candidates"

   // these are optional
   const val LISTENER = "listener"
   const val TERMCOLORS = "termcolors"
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
 * Parses the cli args, creates the listeners and creates a test launcher using a [TestEngineLauncherBuilder].
 *
 * --- IMPORTANT NOTE ---
 * This is used by the Gradle and Intellij plugins (and other third party clients).
 * Therefore, the package name and contract for this main method *MUST* remain backwards compatible.
 */
fun main(args: Array<String>) {

   println("Starting Kotest launcher with args: ${args.joinToString(";")}")

   val launcherArgs = parseArgs(args.toList())
   println("Parsed args: $launcherArgs")

   // The launcher *must* be told what classes are available on the classpath, the engine will not perform scanning.
   // It is the responsibility of the caller to pass this information.
   // In Kotest 5 the argument was called --spec but was changed to --candidates in Kotest 6,
   // we must support both for backwards compatibility
   val candidatesArg = launcherArgs[CANDIDATES]
      ?: launcherArgs[SPEC]
      ?: error("The $CANDIDATES arg must be provided")

   @Suppress("UNCHECKED_CAST")
   val classes = candidatesArg.split(';').map { Class.forName(it).kotlin as KClass<out Spec> }

   // we support --descriptor to support an exact descriptor path as a way to run a single test
   val descriptorFilter = launcherArgs[DESCRIPTOR]?.let { descriptor ->
      println("Making a filter from input $descriptor")
      ProvidedDescriptorFilter(DescriptorPaths.parse(descriptor))
   }

   // Kotest 5 supported --testpath and didn't support the a descriptor selector, only the test name
   // but we can combine that with the --spec arg which we know must be present in kotest 5 if testpath is
   val descriptorFilterKotest5 = launcherArgs[TESTPATH]?.let { test ->
      launcherArgs[SPEC]?.let { spec ->
         ProvidedDescriptorFilter(DescriptorPaths.parse("$spec/$test"))
      }
   }

   val console = TestEngineListenerBuilder.builder()
      .withType(launcherArgs[LISTENER] ?: launcherArgs[REPORTER] ?: launcherArgs[WRITER]) // sets the output type, will be detected if not specified
      .withTermColors(launcherArgs[TERMCOLORS]) // if using the console, determines the prettiness of the output
      .build()

   // we want to collect the results, so we can check if we need exit with an error
   val collector = CollectingTestEngineListener()

   val launcher = TestEngineLauncherBuilder.builder()
      .withClasses(classes)
      .addListener(LoggingTestEngineListener) // we use this to write to the kotest log file
      .addListener(collector)
      .addListener(ThreadSafeTestEngineListener(PinnedSpecTestEngineListener(console))).build()
      .addExtensions(listOfNotNull(descriptorFilter, descriptorFilterKotest5))

   runBlocking {
      launcher.async()
   }

   // there could be threads in the background that will stop the launcher shutting down
   // for example if a test keeps a thread running,
   // so we must force the exit
   if (collector.errors) exitProcess(-1) else exitProcess(0)
}
