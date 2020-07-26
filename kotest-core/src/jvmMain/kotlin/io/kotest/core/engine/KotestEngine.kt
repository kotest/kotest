package io.kotest.core.engine

import io.kotest.core.Tags
import io.kotest.core.config.Project
import io.kotest.core.extensions.SpecifiedTagsTagExtension
import io.kotest.core.filters.TestCaseFilter
import io.kotest.core.internal.NamedThreadFactory
import io.kotest.core.listeners.Listener
import io.kotest.core.runtime.afterProject
import io.kotest.core.runtime.beforeProject
import io.kotest.core.spec.Spec
import io.kotest.core.spec.isDoNotParallelize
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.runBlocking
import java.util.Collections.emptyList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.Future
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.reflect.KClass

data class KotestEngineConfig(
   val classes: List<KClass<out Spec>>,
   val filters: List<TestCaseFilter>,
   val listener: TestEngineListener,
   val tags: Tags?
)

class KotestEngine(private val config: KotestEngineConfig) {

   @Deprecated("for backwards compatibility, do not use, will be removed as soon as possible")
   constructor(
      classes: List<KClass<out Spec>>,
      filters: List<TestCaseFilter>,
      tags: Tags?,
      listener: TestEngineListener,
      // added to listeners statically added via Project.add
      listeners: List<Listener> = emptyList()
   ) : this(KotestEngineConfig(classes, filters, listener, tags))

   @Deprecated("for backwards compatibility, do not use, will be removed as soon as possible")
   constructor(
      classes: List<KClass<out Spec>>,
      filters: List<TestCaseFilter>,
      parallelism: Int,
      tags: Tags?,
      listener: TestEngineListener,
      listeners: List<Listener> = emptyList()
   ) : this(classes, filters, tags, listener, listeners)

   private val specExecutor = SpecExecutor(config.listener)

   init {
      Project.registerFilters(config.filters)
      config.tags?.let { Project.registerExtension(SpecifiedTagsTagExtension(it)) }
   }

   fun cleanup() {
      Project.deregisterFilters(config.filters)
   }

   private fun notifyTestEngineListener() = Try { config.listener.engineStarted(config.classes) }

   private fun submitAll() = Try {
      log("Submitting ${config.classes.size} specs")

      // the classes are ordered using an instance of SpecExecutionOrder
      val ordered = Project.specExecutionOrder().sort(config.classes)

      // if parallelize is enabled, then we must order the specs into two sets, depending on if they
      // are thread safe or not.
      val (single, parallel) = if (Project.parallelism() == 1)
         ordered to emptyList()
      else
         ordered.partition { it.isDoNotParallelize() }

      if (parallel.isNotEmpty()) submitBatch(parallel, Project.parallelism())
      if (single.isNotEmpty()) submitBatch(single, 1)
   }

   private fun submitBatch(specs: List<KClass<out Spec>>, parallelism: Int) {
      val executor = Executors.newFixedThreadPool(
         parallelism,
         NamedThreadFactory("kotest-engine-%d")
      )
      specs.forEach { klass ->
         executor.submit {
            future {
               specExecutor.execute(klass)
            }
         }
      }
      executor.shutdown()
      log("Waiting for specs execution to terminate")

      try {
         executor.awaitTermination(1, TimeUnit.DAYS)
         log("Spec executor has terminated")
      } catch (t: InterruptedException) {
         log("Spec executor interrupted", t)
         throw t
      }
   }

   private fun end(errors: List<Throwable>) {
      errors.forEach {
         log("Error during test engine run", it)
         it.printStackTrace()
      }
      config.listener.engineFinished(errors)
      // explicitly exit because we spin up test threads that the user may have put into deadlock
      // exitProcess(if (t == null) 0 else -1)
   }

   suspend fun execute() {
      notifyTestEngineListener()
         .flatMap { Project.listeners().beforeProject() }
         .fold(
            { error ->
               // any exception here is swallowed, as we already have an exception to report
               Project.listeners().afterProject().fold(
                  { end(listOf(error, it)) },
                  {
                     end(it + error)
                  }
               )
               return
            },
            { errors ->
               if (errors.isNotEmpty()) {
                  Project.listeners().afterProject().fold(
                     { end(errors + listOf(it)) },
                     { end(errors + it) }
                  )
                  return
               }


            }
         )

      Try { submitAll() }
         .fold(
            { error ->
               Project.listeners().afterProject().fold(
                  { end(listOf(error, it)) },
                  { end(it + error) }
               )
            },
            {
               // any exception here is used to notify the listener
               Project.listeners().afterProject().fold(
                  { end(listOf(it)) },
                  { end(it) }
               )

            }
         )
   }
}

fun future(f: suspend () -> Unit): Future<Unit> =
   CompletableFuture<Unit>().apply {
      f.startCoroutine(Continuation(EmptyCoroutineContext) { res ->
         res.fold(::complete, ::completeExceptionally)
      })
   }
