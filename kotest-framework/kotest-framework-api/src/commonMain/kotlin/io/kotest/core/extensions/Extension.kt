package io.kotest.core.extensions

/**
 * What is an extension? - An extension allows your code to interact
 * with the Kotest Engine, changing the behavior of the engine
 * at runtime.
 *
 * How do they differ from Listeners? - They differ because listeners
 * are purely "callback only" they are notified of events but they are
 * unable to change the operation of the Engine.
 *
 * Which should I use? - Always use a listener if you can - they are
 * simpler. Only use an extension if you need to adjust the runtime
 * behavior of the engine, such as when writing an advanced plugin.
 */
interface Extension
