package io.kotest.engine.spec.execution

import io.kotest.common.platform
import io.kotest.common.reflection.annotation
import io.kotest.common.reflection.bestName
import io.kotest.common.reflection.instantiations
import io.kotest.core.Logger
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.SpecRefExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineContext
import io.kotest.engine.flatMap
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRefInflator
import io.kotest.engine.spec.execution.enabled.EnabledOrDisabled
import io.kotest.engine.spec.execution.enabled.SpecRefEnabledChecker
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

/**
 * Executes a [SpecRef].
 *
 * First checks for eligibility that this spec can run, checking various mechanisms such as
 * tags, filters, and annotations, via a [SpecRefEnabledChecker].
 *
 * Then any extensions via [ApplyExtension] annotations are registered.
 *
 * Next, it invokes the appropriate lifecycle callbacks for the spec.
 *
 * Finally, we instantiate the spec to see which isolation mode it is using, and then
 * execute the spec using the appropriate [SpecExecutor].
 */
internal class SpecRefExecutor(
   private val context: TestEngineContext,
) {

   private val logger = Logger(SpecRefExecutor::class)
   private val extensions = SpecExtensions(context.specConfigResolver, context.projectConfigResolver)
   private val specRefEnabledChecker = SpecRefEnabledChecker(context.projectConfigResolver)

   private val inflator = SpecRefInflator(
      registry = context.registry,
      projectConfigRegistry = context.projectConfigResolver,
      extensions = extensions,
   )

   suspend fun execute(ref: SpecRef) {

      // first check if this spec should be skipped, and if so, mark as ignored and exit
      // otherwise proceed to set up the spec lifecycle callbacks
      when (val enabled = specRefEnabledChecker.isEnabled(ref)) {

         is EnabledOrDisabled.Disabled -> {
            logger.log { Pair(ref.kclass.bestName(), "Spec is disabled: ${enabled.reason}") }
            runCatching { context.listener.specIgnored(ref.kclass, enabled.reason) }
               .flatMap { extensions.ignored(ref.kclass, enabled.reason) }
         }

         EnabledOrDisabled.Enabled -> {
            logger.log { Pair(ref.kclass.bestName(), "Spec is enabled") }
            applyExtensions(ref)
         }
      }
   }

   /**
    * Applies any extensions that have been annotated with [ApplyExtension] by adding them to the context
    * registry, and removes them after the spec has been executed.
    */
   private suspend fun applyExtensions(ref: SpecRef) {
      try {

         val classes = ref.kclass.annotation<ApplyExtension>()?.extensions?.toList() ?: emptyList()
         val extensions = classes.map { instantiations.newInstanceNoArgConstructorOrObjectInstance(it) }
         logger.log { Pair(ref.kclass.bestName(), "Applying extensions: $extensions") }

         extensions.forEach {
            logger.log { Pair(ref.kclass.bestName(), "Registering extension $it") }
            context.registry.add(it, ref.kclass)
         }

         invokeSpecRefExtensions(ref)

         extensions.forEach {
            logger.log { Pair(ref.kclass.bestName(), "Removing extension $it") }
            context.registry.remove(it, ref.kclass)
         }

      } catch (t: Throwable) {
         // the spec would not have been started at this point, so we need to start it so we can mark it as failed
         context.listener.specStarted(ref)
         context.listener.specFinished(ref, TestResultBuilder.builder().withError(t).build())
      }
   }

   private suspend fun invokeSpecRefExtensions(ref: SpecRef) {
      val exts = context.projectConfigResolver.extensions().filterIsInstance<SpecRefExtension>()
      logger.log { Pair(ref.kclass.bestName(), "Invoking SpecRefExtensions: $exts") }

      val inner: suspend (SpecRef) -> Unit = {
         invokeEngineListeners(ref)
      }

      val chain = exts.foldRight(inner) { op, acc -> { op.intercept(ref) { acc(ref) } } }
      chain.invoke(ref)
   }

   /**
    * Executes the engine callbacks for the spec.
    *
    * Any errors here will be swallowed and logged as we cannot do anything about them.
    */
   private suspend fun invokeEngineListeners(ref: SpecRef) {
      try {
         context.listener.specStarted(ref)
         val (duration, _, err) = invokeUserListeners(ref)
         context.listener.specFinished(ref, TestResultBuilder.builder().withDuration(duration).withError(err).build())
      } catch (t: Throwable) {
         // means an error in our internal listeners notifying junit etc
         // not much we can do here because if we failed to notify junit of starting or finishing,
         // we can't very well notify it again that we failed
         t.printStackTrace()
      }
   }

   /**
    * Executes the lifecycle callbacks for the reference, [io.kotest.core.listeners.PrepareSpecListener]
    * and [io.kotest.core.listeners.FinalizeSpecListener].
    *
    * Any errors in these listeners will cause the spec to be aborted and marked as failed.
    */
   private suspend fun invokeUserListeners(ref: SpecRef): Triple<Duration, Map<TestCase, TestResult>, Throwable?> {
      return try {
         extensions.prepareSpec(ref.kclass)
         val (results, duration) = measureTimedValue {
            inflateAndExecute(ref)
         }
         extensions.finalizeSpec(ref.kclass, results).getOrThrow()
         Triple(duration, results, null)
      } catch (t: Throwable) {
         Triple(0.seconds, emptyMap(), t)
      }
   }

   /**
    * Inflate's the [SpecRef] to see what isolation mode is configured, and then executes
    * the spec using the appropriate [SpecExecutor].
    */
   private suspend fun inflateAndExecute(ref: SpecRef): Map<TestCase, TestResult> {
      val spec = inflator.inflate(ref).getOrThrow()
      val executor = specExecutor(context, spec)
      logger.log { Pair(ref.kclass.bestName(), "Found executor $executor for platform $platform") }
      return executor.execute(ref, spec).getOrThrow()
   }
}

/**
 * Returns a [SpecExecutor] for the given [Spec] suitable for the current platform.
 * For example, on the JVM it would take into account isolation modes, and on Wasm it will
 * detect if we have a JS hosted environment.
 */
internal expect fun specExecutor(context: TestEngineContext, spec: Spec): SpecExecutor

/**
 * Used to test a [SpecRefExecutor] from another module.
 * Should not be used by user's code and is subject to change.
 */
internal suspend fun testSpecExecutor(
   context: TestEngineContext,
   ref: SpecRef.Reference
) {
   SpecRefExecutor(context).execute(ref)
}
