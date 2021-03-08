package io.kotest.mpp

import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
actual val reflection: Reflection = BasicReflection
