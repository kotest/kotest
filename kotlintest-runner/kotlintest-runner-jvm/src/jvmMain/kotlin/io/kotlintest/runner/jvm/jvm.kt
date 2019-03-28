package io.kotlintest.runner.jvm

import arrow.core.Try
import io.kotlintest.AbstractSpec
import io.kotlintest.Project
import io.kotlintest.Spec
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

fun <T : Spec> instantiateSpec(clazz: KClass<T>): Try<Spec> = Try {

  val nullSpec: Spec? = null

  val instance = Project.constructorExtensions().fold(nullSpec) { spec, ext -> spec ?: ext.instantiate(clazz) }
      ?: clazz.createInstance()

  // after the class is created we no longer allow new top level tests to be added
  if (instance is AbstractSpec) {
    instance.acceptingTopLevelRegistration = false
  }

  instance
}