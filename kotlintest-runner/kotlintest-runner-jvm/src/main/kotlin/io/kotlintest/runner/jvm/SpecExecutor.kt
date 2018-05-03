package io.kotlintest.runner.jvm

import createSpecInterceptorChain
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext

abstract class SpecExecutor(val listener: TestEngineListener) {

  abstract fun execute(spec: Spec)

  protected fun interceptSpec(spec: Spec, afterInterception: () -> Unit) {

    val listeners = listOf(spec) + spec.listeners() + Project.listeners()
    listeners.forEach { it.beforeSpec(spec.description(), spec) }

    val extensions = spec.extensions().filterIsInstance<SpecExtension>() + Project.specExtensions()
    val context = SpecInterceptContext(spec.description(), spec)
    val chain = createSpecInterceptorChain(context, extensions) { afterInterception() }
    chain.invoke()

    listeners.reversed().forEach { it.afterSpec(spec.description(), spec) }
  }
}