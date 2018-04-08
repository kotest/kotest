package io.kotlintest

abstract class DefaultTestContext(val scope: TestScope) : TestContext {

  private val metadata = mutableListOf<Any>()
  private val scopes = mutableListOf<TestScope>()

  override fun addScope(scope: TestScope): TestScope {
    scopes.add(scope)
    return scope
  }

  fun scopes(): List<TestScope> = scopes.toList()

  override fun withMetaData(meta: Any) {
    metadata.add(meta)
  }

  override fun metaData(): List<Any> = metadata.toList()

  override fun currentScope(): TestScope = scope
  override fun description(): Description = scope.description()
}