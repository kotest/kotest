@file:Suppress("FunctionName", "unused")

package io.kotest.core.spec.style

//
//
//@KotestDsl
//class WhenAndContext(val context: TestContext, private val spec: BehaviorSpecDsl) {
//   suspend fun And(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)
//   suspend fun and(name: String, test: suspend WhenAndContext.() -> Unit) = addAndContext(name, test)
//
//   private suspend fun addAndContext(name: String, test: suspend WhenAndContext.() -> Unit) {
//      context.registerTestCase(
//         createTestName("And: ", name),
//         { WhenAndContext(this, this@WhenAndContext.spec).test() },
//         spec.defaultConfig(),
//         TestType.Container
//      )
//   }
//
//   suspend fun Then(name: String, test: suspend TerminalScope.() -> Unit) = addThenContext(name, test, true)
//   suspend fun then(name: String, test: suspend TerminalScope.() -> Unit) = addThenContext(name, test, true)
//   suspend fun xthen(name: String, test: suspend TerminalScope.() -> Unit) = addThenContext(name, test, false)
//
//   private suspend fun addThenContext(name: String, test: suspend TerminalScope.() -> Unit, enabled: Boolean) {
//      context.registerTestCase(
//         createTestName("Then: ", name),
//         { TerminalScope(this).test() },
//         if (enabled) spec.defaultConfig() else spec.defaultConfig().copy(enabled = false),
//         TestType.Test
//      )
//   }
//
//   fun then(name: String) = BehaviorSpecDsl.TestScope(name, context, spec)
//   fun Then(name: String) = BehaviorSpecDsl.TestScope(name, context, spec)
//}
