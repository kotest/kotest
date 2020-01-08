package io.kotest.runner.jvm

import arrow.core.Try
import io.kotest.Project
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.specs.AbstractSpec
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

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
