package io.kotlintest.runner.jvm

import io.kotlintest.Spec
import kotlin.reflect.KClass

/**
 * Contains [Spec] classes discovered as part of a discovery request scan.
 */
data class DiscoveryResult(val classes: List<KClass<out Spec>>)
