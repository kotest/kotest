package io.kotest.extensions.testcontainers

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead")
enum class LifecycleMode {
   Spec, EveryTest, Leaf, Root
}

