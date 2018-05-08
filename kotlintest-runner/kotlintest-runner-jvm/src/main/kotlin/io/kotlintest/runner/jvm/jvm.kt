package io.kotlintest.runner.jvm

import arrow.core.Try
import io.kotlintest.Project
import io.kotlintest.Spec
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

fun <T : Spec> instantiateSpec(clazz: KClass<T>): Try<Spec> = Try {
  val nullSpec: Spec? = null
  val instance = Project.discoveryExtensions().fold(nullSpec, { spec, ext -> spec ?: ext.instantiate(clazz) })
  instance ?: clazz.createInstance()
}