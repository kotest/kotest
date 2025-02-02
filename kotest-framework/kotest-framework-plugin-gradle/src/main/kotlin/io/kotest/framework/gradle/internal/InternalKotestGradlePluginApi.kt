package io.kotest.framework.gradle.internal

/**
 * Opt-in marker for internal Kotest API.
 */
@RequiresOptIn("This API is for Kotest's own use only. There are no backwards compatibility guarantees.")
annotation class InternalKotestGradlePluginApi
