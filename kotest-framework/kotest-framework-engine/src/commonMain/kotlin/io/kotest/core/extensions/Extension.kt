package io.kotest.core.extensions

/**
 * What is an extension? - An extension allows your code to interact
 * with the Kotest Engine, changing the behavior of the engine
 * at runtime.
 *
 * In Kotest we use the nomenclature `Extension` and `Listener`. `Listeners`
 * are extensions themselves, but we use this term for callbacks that
 * receive events but do not change the behavior of the engine.
 *
 * ### Which should I use?
 *
 * Always use a listener if you can - they are
 * simpler. Only use an extension if you need to adjust the runtime
 * behavior of the engine, such as when writing an advanced plugin.
 */
interface Extension
