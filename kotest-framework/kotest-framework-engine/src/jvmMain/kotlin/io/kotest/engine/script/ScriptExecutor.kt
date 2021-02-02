package io.kotest.engine.script

import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.internal.TestCaseExecutor
import io.kotest.core.plan.Descriptor
import io.kotest.core.plan.toDescriptor
import io.kotest.core.script.ScriptRuntime
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseExecutionListener
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.toTestCase
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.engine.NotificationManager
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

   private val results = ConcurrentHashMap<Descriptor.TestDescriptor, TestResult>()
   private val n = NotificationManager(listener)

   /**
    * Executes the given test [ScriptTemplateWithArgs].
    */
   suspend fun execute(kclass: KClass<out ScriptTemplateWithArgs>): Try<Unit> {
      log("ScriptExecutor: execute [$kclass]")
      ScriptRuntime.reset()
      val descriptor = Descriptor.fromScriptClass(kclass)
      return createInstance(kclass)
         .flatMap { n.specStarted(descriptor) }
         .flatMap { runTests(descriptor) }
         .onFailure { n.specFinished(descriptor, it, emptyMap()) }
         .onSuccess { n.specFinished(descriptor, null, results.toMap()) }
   }

//   /**
//    * Invokes the script by finding and executing the generated main method.
//    */
//   private suspend fun runScript(script: ScriptTemplateWithArgs): Try<Unit> = Try {
////      ScriptRuntime.reset()
//
////      BasicJvmScriptEvaluator().invoke(
////         KJvmCompiledScript(
////            null,
////            ScriptCompilationConfiguration.Default,
////            script.javaClass.name,
////            resultField = null,
////            compiledModule = KJvmCompiledModuleFromClassLoader(this.javaClass.classLoader),
////         ), ScriptEvaluationConfiguration()
////      )
//
//      //
//      //val main = script.javaClass.getMethod("main", Array<String>::class.java)
//      //log("ScriptExecutor: Invoking script main [$main]")
//      //main.invoke(null, emptyArray<String>())
//   }

   /**
    * Executes the tests registered with the script execution runtime.
    */
   private suspend fun runTests(descriptor: Descriptor.SpecDescriptor): Try<Unit> = Try {
      log("ScriptExecutor: Executing tests from script")
      ScriptRuntime.materializeRootTests(descriptor).forEach { testCase ->
         runTest(testCase, coroutineContext)
      }
   }

   /**
    * Creates an instance of the given [ScriptTemplateWithArgs] by delegating to the project constructors,
    * and notifies the [TestEngineListener] of the instantiation event.
    */
   private fun createInstance(kclass: KClass<out ScriptTemplateWithArgs>): Try<ScriptTemplateWithArgs> {
      log("ScriptExecutor: Creating instance of $kclass")
      return createAndInitializeScript(kclass, this.javaClass.classLoader)
   }

   private suspend fun runTest(
      testCase: TestCase,
      coroutineContext: CoroutineContext,
   ) {
      val testExecutor = TestCaseExecutor(object : TestCaseExecutionListener {
         override fun testStarted(testCase: TestCase) {
            listener.testStarted(testCase.descriptor!!)
         }

         override fun testIgnored(testCase: TestCase) {
            listener.testIgnored(testCase.descriptor!!, null)
         }

         override fun testFinished(testCase: TestCase, result: TestResult) {
            listener.testFinished(testCase.descriptor!!, result)
         }
      }, ExecutorExecutionContext, {}, { t, duration -> toTestResult(t, duration) })

      val result = testExecutor.execute(testCase, Context(testCase, coroutineContext))
      results[testCase.description.toDescriptor(testCase.source) as Descriptor.TestDescriptor] = result
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
