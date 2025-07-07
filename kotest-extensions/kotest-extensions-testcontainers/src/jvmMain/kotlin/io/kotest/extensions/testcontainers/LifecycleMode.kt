package io.kotest.extensions.testcontainers

@Deprecated("Use TestContainerProjectExtension or TestContainerSpeccExtension instead")
enum class LifecycleMode {
   Spec, EveryTest, Leaf, Root
}

