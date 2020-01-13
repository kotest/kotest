package io.kotest.runner.jvm

import io.kotest.Project
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.specs.AbstractSpec
import io.kotest.fp.Try
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Creates an instance of a [SpecConfiguration] by delegating to constructor extensions, with
 * a fallback to a reflection based zero-args constructor.
 */
fun <T : SpecConfiguration> instantiateSpec(clazz: KClass<T>): Try<SpecConfiguration> = Try {

   val nullSpec: SpecConfiguration? = null

   val instance = Project.constructorExtensions()
      .fold(nullSpec) { spec, ext -> spec ?: ext.instantiate(clazz) } ?: clazz.createInstance()

   // after the class is created we no longer allow new top level tests to be added
   if (instance is AbstractSpec) {
      instance.acceptingTopLevelRegistration = false
   }

   instance
}
