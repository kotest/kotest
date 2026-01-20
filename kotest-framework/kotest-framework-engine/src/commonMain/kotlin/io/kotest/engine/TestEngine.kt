package io.kotest.engine

import io.kotest.common.JVMOnly
import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.extensions.SpecExecutionOrderExtension
import io.kotest.core.log
import io.kotest.core.project.ProjectContext
import io.kotest.core.project.TestSuite
import io.kotest.engine.config.ProjectConfigDumper
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.extensions.ProjectExtensions
import io.kotest.engine.listener.TestEngineInitializedContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.DefaultSpecExecutionOrderExtension
import io.kotest.engine.tags.TagExpression
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

@KotestInternal
data class TestEngineConfig(
   val listener: TestEngineListener,
   val projectConfig: AbstractProjectConfig?,
   val explicitTags: TagExpression?,
   val registry: ExtensionRegistry,
)

/**
 * Multiplatform Kotest Test Engine.
 *
 * The scheduling of tests is delegated to a [TestSuiteScheduler] which is responsible the ordering
 * and concurrency aspect of spec execution.
 */
@KotestInternal
class TestEngine(private val config: TestEngineConfig) {

   private val logger = Logger(this::class)

   /**
    * Starts execution of the given [TestSuite].
    */
   internal suspend fun execute(suite: TestSuite): EngineResult {
      logger.log { Pair(null, "Initiating test engine with ${suite.specs.size} specs") }

      // must be early so extensions and config resolvers have access to the props
      loadSystemProperties()

      val tags = TagExpression.Empty // todo = config.configuration.runtimeTagExpression()
      logger.log { Pair(null, "TestEngine: Active tags: ${tags.expression}") }

      val context = TestEngineContext.create(
         suite = suite,
         tags = tags,
         registry = config.registry,
         projectConfig = resolveProjectConfig(config.projectConfig),
         listener = config.listener,
      )

      ProjectConfigDumper.dumpConfigIfEnabled(context)

      val result = invokeTestEngineListeners(context)

      writeFailuresIfEnabled(context)
      return TestDslChecker.checkForDslState(EmptyTestSuiteChecker.checkForEmptyTestSuite(context, result))
   }

   internal suspend fun invokeTestEngineListeners(context: TestEngineContext): EngineResult {

      context.listener.engineStarted()

      val result = executeWithProjectTimeout(context)
      result.errors.forEach {
         logger.log { Pair(null, "Error during test engine run: $it") }
         it.printStackTrace()
      }

      context.listener.engineFinished(result.errors)
      return result
   }

   internal suspend fun executeWithProjectTimeout(context: TestEngineContext): EngineResult {
      return when (val timeout = context.projectConfigResolver.projectTimeout()) {
         null -> executeProjectExtensions(context, context.projectConfigResolver)
         else -> try {
            withTimeout(timeout) {
               executeProjectExtensions(context, context.projectConfigResolver)
            }
         } catch (_: TimeoutCancellationException) {
            val e = ProjectTimeoutException(timeout)
            EngineResult(listOf(e), false)
         } catch (t: Throwable) {
            EngineResult(listOf(t), false)
         }
      }
   }

   internal suspend fun executeProjectExtensions(
      context: TestEngineContext,
      projectConfigResolver: ProjectConfigResolver
   ): EngineResult {
      var result: EngineResult = EngineResult.empty
      var projectContext: ProjectContext = context.toProjectContext()
      val initial: suspend (ProjectContext) -> Unit =
         { result = executeProjectListeners(projectContext.toEngineContext(context), projectConfigResolver) }
      val chain: suspend (ProjectContext) -> Unit = projectConfigResolver.extensions()
         .filterIsInstance<ProjectExtension>()
         .foldRight(initial) { extension, acc: suspend (ProjectContext) -> Unit ->
            {
               extension.interceptProject(projectContext) {
                  projectContext = it
                  acc(projectContext)
               }
            }
         }
      return try {
         chain.invoke(context.toProjectContext())
         result
      } catch (t: Throwable) {
         result.addError(t)
      }
   }

   internal suspend fun executeProjectListeners(
      context: TestEngineContext,
      projectConfigResolver: ProjectConfigResolver,
   ): EngineResult {

      val extensions = ProjectExtensions(projectConfigResolver)
      val beforeErrors = extensions.beforeProject()

      // if we have errors in the before project listeners, we'll not execute tests,
      // but instead immediately return those errors.
      if (beforeErrors.isNotEmpty()) return EngineResult(beforeErrors, false)

      val result = sortSuiteAndExecute(context)

      val afterErrors = extensions.afterProject()

      // if we have errors in the after project listeners, we'll add those to any other errors during
      // the test suite execution.
      return result.addErrors(afterErrors)
   }

   internal suspend fun sortSuiteAndExecute(context: TestEngineContext): EngineResult {
      val withSortedSuite = context.copy(suite = sortTestSuite(context.suite, context.projectConfigResolver))
      return execute(withSortedSuite)
   }

   internal suspend fun execute(context: TestEngineContext): EngineResult {
      return try {
         // lets the test engine listeners know that we're all setup and ready to begin the test suite
         context.listener.engineInitialized(
            TestEngineInitializedContext(
               suite = context.suite,
               tags = context.tags,
               registry = context.registry,
               projectConfig = context.projectConfig
            )
         )
         TestSuiteScheduler(context).schedule(context.suite)
      } catch (t: Throwable) {
         EngineResult(listOf(t))
      }
   }

   /**
    * Returns an updated [TestSuite] with specs sorted according to registered [SpecExecutionOrderExtension]s
    * or falling back to the [DefaultSpecExecutionOrderExtension].
    */
   internal fun sortTestSuite(suite: TestSuite, projectConfigResolver: ProjectConfigResolver): TestSuite {

      val exts = projectConfigResolver.extensions().filterIsInstance<SpecExecutionOrderExtension>().ifEmpty {
         listOf(DefaultSpecExecutionOrderExtension(projectConfigResolver))
      }

      log { "Sorting specs using extensions $exts" }
      val specs = exts.fold(suite.specs) { acc, op -> op.sort(acc) }
      return TestSuite(specs)
   }
}

/**
 * Loads ProjectConfig from the class and overrides the default, or returns
 * the given config if no other project is located.
 */
@JVMOnly
internal expect fun resolveProjectConfig(projectConfig: AbstractProjectConfig?): AbstractProjectConfig?

@JVMOnly
internal expect fun writeFailuresIfEnabled(context: TestEngineContext)

/**
 * Loads system properties from a well-known props file from the classpath.
 */
@JVMOnly
internal expect fun loadSystemProperties()
