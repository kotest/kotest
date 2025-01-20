package io.kotest.framework.discovery

import io.kotest.core.spec.Spec

/**
 * [DiscoveryRequest] describes how to discover test classes.
 *
 * Selectors are instances of [DiscoverySelector] and are used to locate [Spec]s. For example,
 * a Spec may be referred to by name via a [DiscoverySelector.ClassDiscoverySelector], or all specs in a
 * package may be referred to via a [DiscoverySelector.PackageDiscoverySelector]. Selectors stack, so each
 * selector may contribute zero or more specs and all discovered specs are returned.
 *
 * If no selectors are provided, then all specs on the class path are returned.
 *
 * Filters are instances of [DiscoveryFilter] and are applied to the discovered set of Specs.
 * All of them have to include a resource for it to end up in the test plan. For example, you may
 * filter specs by a [DiscoveryFilter.ClassNameDiscoveryFilter] where any specs that do not have a matching name
 * are removed. In addition, you could apply a [DiscoveryFilter.PackageNameDiscoveryFilter] and all specs not in
 * the specified packages would be removed.
 */
data class DiscoveryRequest(
   val selectors: List<DiscoverySelector>,
   val filters: List<DiscoveryFilter>,
)

data class DiscoveryRequestBuilder(
   val selectors: List<DiscoverySelector>,
   val filters: List<DiscoveryFilter>,
) {

   companion object {
      fun builder() = DiscoveryRequestBuilder(emptyList(), emptyList())
   }

   fun withSelector(selector: DiscoverySelector): DiscoveryRequestBuilder =
      copy(selectors = this.selectors + selector)

   fun withFilter(filter: DiscoveryFilter): DiscoveryRequestBuilder =
      copy(filters = this.filters + filter)

   fun withSelectors(selectors: List<DiscoverySelector>): DiscoveryRequestBuilder =
      copy(selectors = this.selectors + selectors)

   fun withFilters(filters: List<DiscoveryFilter>): DiscoveryRequestBuilder =
      copy(filters = this.filters + filters)

   fun build(): DiscoveryRequest {
      return DiscoveryRequest(selectors, filters)
   }
}

data class FullyQualifiedClassName(val value: String)
data class PackageName(val value: String)

enum class Modifier {
   Public, Internal, Private
}
