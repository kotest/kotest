package io.kotest.runner.jvm

import io.kotest.core.config.Project
import io.kotest.core.spec.Spec
import io.kotest.fp.Try
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Creates an instance of a [Spec] by delegating to constructor extensions, with
 * a fallback to a reflection based zero-args constructor.
 */
fun <T : Spec> instantiateSpec(clazz: KClass<T>): Try<Spec> = Try {

   val nullSpec: Spec? = null

   val instance = Project.constructorExtensions()
      .fold(nullSpec) { spec, ext -> spec ?: ext.instantiate(clazz) } ?: clazz.createInstance()

//   // after the class is created we no longer allow new top level tests to be added
//   if (instance is AbstractSpec) {
//      instance.acceptingTopLevelRegistration = false
//   }

   instance
}
