package io.kotest.extensions.testcontainers

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
enum class LifecycleMode {
   Spec, EveryTest, Leaf, Root
}

