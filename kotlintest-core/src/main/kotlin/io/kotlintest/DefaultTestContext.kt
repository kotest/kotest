package io.kotlintest

abstract class DefaultTestContext(val scope: TestScope) : TestContext {

  private val metadata = mutableMapOf<String, Any?>()
  private val scopes = mutableListOf<TestScope>()

  override fun executeScope(scope: TestScope): TestScope {
    scopes.add(scope)
    return scope
  }

  fun scopes(): List<TestScope> = scopes.toList()

  override fun putMetaData(key: String, value: Any?) {
    metadata[key] = value
  }

  override fun metaData() = metadata.toMap()

  override fun currentScope(): TestScope = scope
  override fun description(): Description = scope.description()
}