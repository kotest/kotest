package io.kotest.runner.junit4

import io.kotest.core.extensions.Extension
import org.junit.rules.MethodRule
import org.junit.rules.TestRule
import org.junit.runners.model.FrameworkMethod

internal actual fun filters(): List<Extension> = emptyList()

internal actual fun collectTestRules(target: Any): List<TestRule> =
   collectAnnotatedRules(target, target.javaClass) { it as? TestRule }

internal actual fun collectMethodRules(target: Any): List<MethodRule> =
   collectAnnotatedRules(target, target.javaClass) { it as? MethodRule }

internal actual fun syntheticFrameworkMethod(target: Any): FrameworkMethod =
   FrameworkMethod(target.javaClass.getMethod("toString"))
