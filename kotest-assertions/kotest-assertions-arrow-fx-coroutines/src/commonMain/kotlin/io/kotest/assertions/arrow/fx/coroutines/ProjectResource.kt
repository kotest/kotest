package io.kotest.assertions.arrow.fx.coroutines

import arrow.fx.coroutines.Resource
import io.kotest.core.extensions.LazyMaterialized
import io.kotest.core.listeners.ProjectListener

/**
 * Useful to register [Resource] on a Project wide configuration.
 *
 * For example:
 *
 * ```kotlin
 * object ProjectConfig : AbstractProjectConfig() {
 *   val hikari: ProjectResource<HikariDataSource> =
 *     ProjectResource(Resource.fromCloseable { HikariDataSource(...) })
 *
 *   override fun extensions(): List<Extension> = listOf(hikari)
 * }
 *
 * class MySpec : StringSpec({
 *   "my test" {
 *     val dataSource: HikariDataSource = ProjectConfig.hikari.get()
 *   }
 * })
 * ```
 */
public class ProjectResource<A> private constructor(
  public val resource: Resource<A>,
  private val default: ResourceLazyMaterialized<A>
) : ProjectListener, LazyMaterialized<A> by default {

  public constructor(resource: Resource<A>) : this(resource, ResourceLazyMaterialized(resource))

  override suspend fun beforeProject() {
    default.init()
  }

  override suspend fun afterProject() {
    default.release()
    default.close()
  }
}
