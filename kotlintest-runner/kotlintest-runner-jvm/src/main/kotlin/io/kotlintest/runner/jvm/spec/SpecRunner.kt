package io.kotlintest.runner.jvm.spec

import createSpecInterceptorChain
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.runner.jvm.TestEngineListener

abstract class SpecRunner(val listener: TestEngineListener) {

  abstract fun execute(spec: Spec, active: List<TestCase>, inactive: List<TestCase>): Map<TestCase, TestResult>

  protected fun interceptSpec(spec: Spec, afterInterception: () -> Unit) {

    val listeners = listOf(spec) + spec.listeners() + Project.listeners()
    listeners.forEach {
      it.beforeSpec(spec.description(), spec)
      it.beforeSpec(spec)
    }

    val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
    val context = SpecInterceptContext(spec.description(), spec)
    val chain = createSpecInterceptorChain(context, extensions) { afterInterception() }
    chain.invoke()

    listeners.reversed().forEach {
      it.afterSpec(spec.description(), spec)
      it.afterSpec(spec)
    }
  }
}