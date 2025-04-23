//package io.kotest.engine.spec
//
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.spec.Spec
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestResult
//import io.kotest.engine.config.SpecConfigResolver
//import io.kotest.engine.interceptors.EngineContext
//import io.kotest.engine.kotlinJsTestFramework
//import io.kotest.engine.spec.interceptor.SpecContext
//import io.kotest.engine.test.NoopTestCaseExecutionListener
//import io.kotest.engine.test.TestCaseExecutor
//import io.kotest.engine.test.names.TestNameEscaper
//import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
//import io.kotest.engine.test.scopes.TerminalTestScope
//import io.kotest.engine.test.status.isEnabledInternal
//import io.kotest.engine.testFunctionPromise
//import io.kotest.mpp.bestName
//import kotlinx.coroutines.DelicateCoroutinesApi
//import kotlinx.coroutines.GlobalScope
//import kotlin.coroutines.coroutineContext
//
///**
// * A [SpecExecutorDelegate] running tests via the Kotlin test infra for JS-hosted targets.
// */
//@Suppress("DEPRECATION")
//@ExperimentalKotest
//internal class KotlinJsTestSpecExecutorDelegate(private val context: EngineContext) : SpecExecutorDelegate {
//
//   private val formatter = getFallbackDisplayNameFormatter(
//      context.projectConfigResolver,
//      context.testConfigResolver,
//   )
//
//   private val materializer = Materializer(SpecConfigResolver(context.projectConfig))
//
//   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
//      val cc = coroutineContext
//      val specContext = SpecContext.create()
//      // This implementation supports a two-level test hierarchy with the spec itself as the test `suite`,
//      // which declares a single level of `test`s.
//      kotlinJsTestFramework.suite(TestNameEscaper.escape(spec::class.bestName()), ignored = false) {
//         materializer.materialize(spec).forEach { testCase ->
//            kotlinJsTestFramework.test(
//               TestNameEscaper.escape(formatter.format(testCase)),
//               ignored = testCase.isEnabledInternal(
//                  context.projectConfigResolver,
//                  context.testConfigResolver
//               ).isDisabled
//            ) {
//               // We rely on JS Promise to interact with the JS test framework. We cannot use callbacks here
//               // because we pass our function through the Kotlin/JS test infra via its interface `FrameworkAdapter`,
//               // which does not support callbacks. It does, however, allow the test function to return a Promise-like
//               // type for asynchronous invocations. See `KotlinJsTestFramework` for details.
//               @OptIn(DelicateCoroutinesApi::class)
//               GlobalScope.testFunctionPromise {
//                  TestCaseExecutor(NoopTestCaseExecutionListener, context)
//                     .execute(testCase, TerminalTestScope(testCase, cc), specContext)
//                     .errorOrNull?.let { throw it }
//               }
//            }
//         }
//      }
//      return emptyMap()
//   }
//}
