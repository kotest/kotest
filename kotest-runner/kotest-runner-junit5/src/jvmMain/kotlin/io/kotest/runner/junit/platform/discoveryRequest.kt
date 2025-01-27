package io.kotest.runner.junit.platform

import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.launcher.EngineFilter
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.PostDiscoveryFilter

/**
 * If this [EngineDiscoveryRequest] is a [LauncherDiscoveryRequest] then returns any [EngineFilter]s.
 */
internal fun EngineDiscoveryRequest.engineFilters(): List<EngineFilter> = when (this) {
   is LauncherDiscoveryRequest -> engineFilters
   else -> emptyList()
}

/**
 * If this [EngineDiscoveryRequest] is a [LauncherDiscoveryRequest] then returns any [PostDiscoveryFilter]s.
 */
internal fun EngineDiscoveryRequest.postFilters(): List<PostDiscoveryFilter> = when (this) {
   is LauncherDiscoveryRequest -> postDiscoveryFilters
   else -> emptyList()
}
