@file:Suppress("unused", "DEPRECATION")

package io.kotest.core.spec

// these are backwards compatible typealiases

@Deprecated("This annotation has been moved to io.kotest.core.annotation.EnabledIf. Deprecated in 6.0.", ReplaceWith("io.kotest.core.annotation.EnabledIf"))
typealias DisplayName = io.kotest.core.annotation.DisplayName

@Deprecated("This annotation has been deprecated and replaced with Isolate. Deprecated in 6.0.", ReplaceWith("io.kotest.core.annotation.Isolate"))
typealias DoNotParallelize = io.kotest.core.annotation.DoNotParallelize

@Deprecated("This annotation has been moved to io.kotest.core.annotation.Isolate Deprecated. in 6.0.", ReplaceWith("io.kotest.core.annotation.Isolate"))
typealias Isolate = io.kotest.core.annotation.Isolate
