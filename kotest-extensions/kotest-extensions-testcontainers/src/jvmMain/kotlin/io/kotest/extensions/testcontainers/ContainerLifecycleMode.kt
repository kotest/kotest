package io.kotest.extensions.testcontainers

/**
 * Determines the lifetime of a test container installed in a Kotest extension.
 */
@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead")
enum class ContainerLifecycleMode {

   /**
    * The TestContainer is started when first installed, and then stopped and restarted before each test.
    *
    * Use this when you want a refresh test container per test. Warning - this will increase your test
    * suite time significantly, as the container will need to be stopped and restarted for every test.
    */
   Test,

   /**
    * The TestContainer is started only when first installed and stopped after the spec where it was
    * installed completes.
    *
    * Use this when you need the test container to shut down as soon as the spec does - usually
    * because you are using a separate test container per spec and waiting until the test suite
    * completes to shut them all down will take too much memory.
    */
   Spec,

   /**
    * The TestContainer is started only when first installed and stopped after the entire test suite.
    * This mode is the default choice for test containers. This mode can be used with
    * multiple test containers by using separate instances of the container extensions.
    */
   Project,
}
