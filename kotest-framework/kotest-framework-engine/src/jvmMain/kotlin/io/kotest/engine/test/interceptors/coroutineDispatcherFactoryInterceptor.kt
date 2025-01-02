//package io.kotest.engine.test.interceptors
//
//import io.kotest.common.JVMOnly
//import io.kotest.core.concurrency.CoroutineDispatcherFactory
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestResult
//import io.kotest.core.test.TestScope
//import io.kotest.engine.concurrency.FixedThreadCoroutineDispatcherFactory
//import io.kotest.engine.test.scopes.withCoroutineContext
//import io.kotest.core.Logger
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.test.TestDispatcher
//import kotlin.coroutines.coroutineContext
//
//@ExperimentalStdlibApi
//@JVMOnly
//internal actual fun coroutineDispatcherFactoryInterceptor(
//   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
//): TestExecutionInterceptor = CoroutineDispatcherFactoryInterceptor(defaultCoroutineDispatcherFactory)
//
///**
// * Switches execution onto a dispatcher provided by a [CoroutineDispatcherFactory].
// *
// * If the coroutine is an instance of [TestDispatcher] then the coroutine will not be changed.
// */
//@ExperimentalStdlibApi
//internal class CoroutineDispatcherFactoryInterceptor(
//   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
//) : TestExecutionInterceptor {
//
//   private val logger = Logger(CoroutineDispatcherFactoryInterceptor::class)
//
//   override suspend fun intercept(
//      testCase: TestCase,
//      scope: TestScope,
//      test: NextTestExecutionInterceptor
//   ): TestResult {
//
//      val currentDispatcher = coroutineContext[CoroutineDispatcher]
//      // we don't override if we've set a test dispatcher on this already
//      return if (currentDispatcher is TestDispatcher) {
//         test(testCase, scope)
//      } else {
//
//         val userFactory = testCase.spec.coroutineDispatcherFactory ?: testCase.spec.coroutineDispatcherFactory()
//         val threads = testCase.spec.threads ?: testCase.spec.threads() ?: 1
//
//         logger.log { Pair(testCase.name.name, "userFactory=$userFactory; threads=$threads") }
//
//         val (factory, factoryIsEphemeral) = when {
//            userFactory != null -> Pair(userFactory, false)
//            threads > 1 -> Pair(FixedThreadCoroutineDispatcherFactory(threads, false), true)
//            else -> Pair(defaultCoroutineDispatcherFactory, false)
//         }
//
//         try {
//            logger.log { Pair(testCase.name.name, "Switching dispatcher using factory $factory") }
//            factory.withDispatcher(testCase) {
//               test(testCase, scope.withCoroutineContext(coroutineContext))
//            }
//         } finally {
//            if (factoryIsEphemeral) {
//               factory.close()
//            }
//         }
//      }
//   }
//}
