package io.kotest.engine.spec.execution.enabled

import io.kotest.engine.config.ProjectConfigResolver

internal actual fun platformSpecRefEnabledExtensions(projectConfigResolver: ProjectConfigResolver): List<SpecRefEnabledExtension> {
   return emptyList()
}
