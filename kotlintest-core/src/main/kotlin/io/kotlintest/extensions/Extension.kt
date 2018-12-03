package io.kotlintest.extensions

/**
 * What is an extension? - An extension allows custom code to interact
 * with the kotlintest Engine, changing it's behavior at runtime.
 *
 * How do they differ from Listeners? - They differ because listeners
 * are purely "callback only", in that they are notified of events
 * as they happen, but they have no control over how the engine
 * is operating.
 *
 * Which should I use? - Use a listener as they are simpler, unless
 * you need to change behavior at runtime - for example, adding a tag
 * from a database, ignoring a test dynamically, and so on.
 */
interface Extension

/**
 * Marker interface for extensions that can be added project wide.
 *
 */
interface ProjectLevelExtension : Extension

/**
 * Marker interface for extensions that can be added to specs.
 */
interface SpecLevelExtension : Extension