package io.kotest.core.spec

import io.kotest.core.annotation.DisplayName
import io.kotest.core.annotation.DoNotParallelize

// these are backwards compatible typealiases

@Deprecated("Use io.kotest.core.annotation.DisplayName instead. Deprecated since 6.0.", ReplaceWith("io.kotest.core.annotation.DisplayName"))
typealias DisplayName = DisplayName

@Deprecated("Use io.kotest.core.annotation.DoNotParallelize instead. Deprecated since 6.0.", ReplaceWith("io.kotest.core.annotation.DoNotParallelize"))
typealias DoNotParallelize = DoNotParallelize

@Deprecated("Use io.kotest.core.annotation.Isolate instead. Deprecated since 6.0.", ReplaceWith("io.kotest.core.annotation.Isolate"))
typealias Isolate = io.kotest.core.annotation.Isolate
