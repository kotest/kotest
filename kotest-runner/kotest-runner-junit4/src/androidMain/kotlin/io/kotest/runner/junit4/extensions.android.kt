package io.kotest.runner.junit4

import io.kotest.core.extensions.Extension
import org.junit.Rule
import org.junit.runners.model.FrameworkMethod
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass

internal actual fun filters(): List<Extension> = listOf(InstrumentationFilter)

internal actual fun syntheticFrameworkMethod(target: Any): FrameworkMethod =
   FrameworkMethod(target.javaClass.getMethod("toString"))

internal actual fun hasRule(annotations: Array<Annotation>): Boolean = annotations.any { it is Rule }

internal actual fun classFor(target: KClass<*>): Class<*> = target.java
internal actual fun fields(target: Class<*>): List<Field> = target.declaredFields.toList()
internal actual fun methods(target: Class<*>): List<Method> = target.declaredMethods.toList()
