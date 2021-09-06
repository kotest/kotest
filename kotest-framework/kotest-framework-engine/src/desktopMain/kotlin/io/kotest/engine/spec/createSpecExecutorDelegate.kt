package io.kotest.engine.spec

import io.kotest.engine.listener.TestEngineListener

actual fun createSpecExecutorDelegate(listener: TestEngineListener): SpecExecutorDelegate {
   TODO("Not yet implemented")
}
//
//actual fun execute(spec: Spec, onComplete: suspend () -> Unit) {
//   log { "Executing spec $spec" }
//   println()
//   println(
//      TeamCityMessageBuilder.testSuiteStarted(spec::class.toDescription().displayName())
//         .id(spec::class.toDescription().id.value)
//         .spec()
//         .build()
//   )
//   println()
//   spec.materializeAndOrderRootTests()
//      .filter { it.testCase.isEnabledInternal().isEnabled }
//      .forEach { execute(it.testCase) }
//   println()
//   println(
//      TeamCityMessageBuilder.testSuiteFinished(spec::class.toDescription().displayName())
//         .id(spec::class.toDescription().id.value)
//         .spec()
//         .build()
//   )
//   println()
//   runBlocking {
//      onComplete()
//   }
//}
//
//private fun execute(testCase: TestCase) = runBlocking {
//   log { "Executing testCase $testCase" }
//   val context = RootRestrictedTestContext(testCase, coroutineContext)
//   TestCaseExecutor(TeamCityTestCaseExecutionListener, CallingThreadExecutionContext)
//      .execute(testCase, context)
//}
