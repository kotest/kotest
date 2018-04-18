package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.Spec
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

fun <T : Spec> createSpecInstance(clazz: KClass<T>): Spec {
  val nullSpec: Spec? = null
  val instance = Project.discoveryExtensions().fold(nullSpec, { spec, ext -> spec ?: ext.instantiate(clazz) })
  return instance ?: clazz.createInstance()
}