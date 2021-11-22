package io.kotest.core.listeners

/**
 * Brings together [BeforeProjectListener] and [AfterProjectListener]. Exists for historical reasons.
 * Users can choose to extend this, or the constituent interfaces.
 */
interface ProjectListener : BeforeProjectListener, AfterProjectListener
