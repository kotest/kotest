package io.kotlintest

abstract class DefaultTestContext(val scope: Scope) : TestContext {

  private val metadata = mutableMapOf<String, Any?>()
  private val scopes = mutableListOf<Scope>()

  override fun executeScope(scope: Scope): Scope {
    scopes.add(scope)
    return scope
  }

  fun scopes(): List<Scope> = scopes.toList()

  override fun putMetaData(key: String, value: Any?) {
    metadata[key] = value
  }

  override fun metaData() = metadata.toMap()

  override fun currentScope(): Scope = scope
  override fun description(): Description = scope.description()
}