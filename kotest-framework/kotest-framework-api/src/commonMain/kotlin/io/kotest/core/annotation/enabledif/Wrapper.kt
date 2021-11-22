package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.annotation.EnabledIf
import kotlin.reflect.KClass

expect val EnabledIf.wrapper: KClass<out EnabledCondition>
