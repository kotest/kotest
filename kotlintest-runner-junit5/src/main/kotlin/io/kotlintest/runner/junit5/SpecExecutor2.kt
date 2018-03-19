//package io.kotlintest.runner.junit5
//
//import io.kotlintest.AbstractSpec
//import io.kotlintest.Spec
//import org.junit.platform.engine.EngineExecutionListener
//import org.junit.platform.engine.TestExecutionResult
//import org.junit.platform.engine.UniqueId
//
//class SpecExecutor(val listener: EngineExecutionListener) {
//
//  fun execute(descriptor: TestContainerDescriptor) {
//    try {
//      listener.executionStarted(descriptor)
//      descriptor.children.forEach {
//        when (it) {
//          is TestContainerDescriptor -> execute(it)
//          is TestCaseDescriptor -> execute(it)
//          else -> throw IllegalStateException("$it is not supported")
//        }
//      }
//      listener.executionFinished(descriptor, TestExecutionResult.successful())
//    } catch (throwable: Throwable) {
//      listener.executionFinished(descriptor, TestExecutionResult.failed(throwable))
//    }
//  }
//
//  private fun execute(descriptor: TestCaseDescriptor) {
//
//
//  }
//
//  private fun execute(specDescriptor: TestCaseDescriptor, listener: EngineExecutionListener) {
//    try {
//      interceptorChain(specDescriptor.spec).invoke(specDescriptor.spec, {
//        run(specDescriptor, listener)
//      })
//    } catch (throwable: Throwable) {
//      // an exception here means the entire spec failed
//      listener.executionFinished(specDescriptor, TestExecutionResult.failed(throwable))
//    }
//  }
//}
//
//}
//
//// a basic implementation that supports spec interceptors, traverses the specs, and for each detected test invokes 'run'
//abstract class AbstractSpecExecutor2 : SpecExecutor2 {
//
//  protected abstract fun run(testCase: TestCaseDescriptor, listener: EngineExecutionListener)
//
//
//
//  private fun interceptorChain(spec: AbstractSpec) = createInterceptorChain(spec.specInterceptors, initialInterceptor)
//}