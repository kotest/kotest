package io.kotest.extensions


/**
 * What is an extension? - An extension allows your code to interact
 * with the Kotest Engine, changing the behavior of the engine
 * at runtime.
 *
 * How do they differ from Listeners? - They differ because listeners
 * are purely "callback only" - in that they are notified of events
 * as they happen, but they have no control over how the engine
 * is operating.
 *
 * Which should I use? - Always use a listener if you can - they are
 * simpler. Only use an extension if you need to, for example, with
 * an extension you can dynamically filter tests at runtime, or you can
 * add tags to tests from a database.
 */
interface Extension

/**
 * Marker interface for extensions that can be added project wide.
 */
interface ProjectLevelExtension : Extension

/**
 * Marker interface for extensions that can be added to specs.
 */
interface SpecLevelExtension : Extension