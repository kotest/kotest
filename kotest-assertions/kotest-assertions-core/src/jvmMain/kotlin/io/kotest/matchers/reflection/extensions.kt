package io.kotest.matchers.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.memberProperties

internal fun KClass<*>.findFunction(name: String) = declaredFunctions.firstOrNull { it.name == name }
internal fun KClass<*>.findMemberProperty(name: String) = memberProperties.firstOrNull { it.name == name }
internal fun KVisibility.humanName() = name.toLowerCase().capitalize()