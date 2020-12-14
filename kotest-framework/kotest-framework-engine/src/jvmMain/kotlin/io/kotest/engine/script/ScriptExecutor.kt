package io.kotest.engine.script

import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.config.configuration
import io.kotest.core.config.specInstantiationListeners
import io.kotest.core.internal.TestCaseExecutor
import io.kotest.core.script.ScriptRuntime
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseExecutionListener
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.toTestCase
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.toTestResult
import io.kotest.fp.Try
import io.kotest.mpp.log
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KClass
import kotlin.script.templates.standard.ScriptTemplateWithArgs

/**
 * Handles the execution of a single [ScriptTemplateWithArgs] class.
 *
 * @param listener a listener that is notified of events in the spec lifecycle
 */
class ScriptExecutor(private val listener: TestEngineListener) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()

   /**
    * Executes the given test [ScriptTemplateWithArgs].
    */
   suspend fun execute(kclass: KClass<out ScriptTemplateWithArgs>): Try<Unit> {
      log("ScriptExecutor: execute [$kclass]")
      ScriptRuntime.reset()
      return createInstance(kclass)
         .flatMap { runTests() }
   }

   /**
    * Invokes the script by finding and executing the generated main method.
    */
   private suspend fun runScript(script: ScriptTemplateWithArgs): Try<Unit> = Try {
//      ScriptRuntime.reset()

//      BasicJvmScriptEvaluator().invoke(
//         KJvmCompiledScript(
//            null,
//            ScriptCompilationConfiguration.Default,
//            script.javaClass.name,
//            resultField = null,
//            compiledModule = KJvmCompiledModuleFromClassLoader(this.javaClass.classLoader),
//         ), ScriptEvaluationConfiguration()
//      )

      //
      //val main = script.javaClass.getMethod("main", Array<String>::class.java)
      //log("ScriptExecutor: Invoking script main [$main]")
      //main.invoke(null, emptyArray<String>())
   }

   /**
    * Executes the tests registered with the script execution runtime.
    */
   private suspend fun runTests() = Try {
      log("ScriptExecutor: Executing tests from script")
      ScriptRuntime.materializeRootTests().forEach { testCase ->
         runTest(testCase, coroutineContext)
      }
   }

   /**
    * Notifies the [TestEngineListener] that we are about to start execution of a [ScriptTemplateWithArgs].
    * This is called only once per class regardless of the number of instantiation events.
    */
   private fun notifySpecStarted(kclass: KClass<out Spec>) = Try {
      log("ScriptExecutor: Executing engine listener callback:specStarted $kclass")
      listener.testStarted(kclass.toDescription())

   }

   private fun notifySpecInstantiated(spec: Spec) = Try {
      log("ScriptExecutor: Executing engine listener callback:specInstantiated spec:$spec")
      val listeners = configuration.specInstantiationListeners()
      listener.specInstantiated(spec)
      listeners.forEach {
         it.specInstantiated(spec)
      }
   }

   /**
    * Creates an instance of the given [ScriptTemplateWithArgs] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   private fun createInstance(kclass: KClass<out ScriptTemplateWithArgs>): Try<ScriptTemplateWithArgs> {
      log("ScriptExecutor: Creating instance of $kclass")
      return createAndInitializeScript(kclass, this.javaClass.classLoader).apply { log("Qeqewe $this") }
   }

   private suspend fun runTest(
      testCase: TestCase,
      coroutineContext: CoroutineContext,
   ) {
      val testExecutor = TestCaseExecutor(object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {
            listener.testStarted(testCase)
         }

         override fun testIgnored(testCase: TestCase) {
            listener.testIgnored(testCase, null)
         }

         override fun testFinished(testCase: TestCase, result: TestResult) {
            listener.testFinished(testCase, result)
         }
      }, ExecutorExecutionContext, {}, { t, duration -> toTestResult(t, duration) })

      val result = testExecutor.execute(testCase, Context(testCase, coroutineContext))
      results[testCase] = result
   }

   inner class Context(
      override val testCase: TestCase,
      override val coroutineContext: CoroutineContext,
   ) : TestContext {

      // these are the tests inside this context, so we can track for duplicates
      private val seen = mutableSetOf<DescriptionName.TestName>()

      // in the single instance runner we execute each nested test as soon as they are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         log("Nested test case discovered $nested")
         val nestedTestCase = nested.toTestCase(testCase.spec, testCase.description)
         if (seen.contains(nested.name))
            throw DuplicatedTestNameException(nested.name)
         seen.add(nested.name)
         runTest(nestedTestCase, coroutineContext)
      }
   }
}
