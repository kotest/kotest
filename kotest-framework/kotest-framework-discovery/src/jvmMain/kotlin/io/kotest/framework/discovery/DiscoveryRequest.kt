package io.kotest.framework.discovery

/**
 * [DiscoveryRequest] describes how to discover test classes.
 *
 * Selectors are instances of [DiscoverySelector] and are used to locate [Spec]s. For example,
 * a Spec may be referred to by name via a [DiscoverySelector.ClassDiscoverySelector], or all specs in a
 * package may be referred to via a [DiscoverySelector.PackageDiscoverySelector]. Selectors stack, so each
 * selector may contribute zero or more specs and all discovered specs are returned.
 *
 * Filters are instances of [DiscoveryFilter] and are applied to the discovered set of Specs.
 * All of them have to include a resource for it to end up in the test plan. For example, you may
 * filter specs by a [DiscoveryFilter.ClassNameDiscoveryFilter] where any specs that do not have a matching name
 * are removed. In addition, you could apply a [DiscoveryFilter.PackageNameDiscoveryFilter] and all specs not in
 * the specified packages would be removed.
 */
data class DiscoveryRequest(
   val selectors: List<DiscoverySelector> = emptyList(),
   val filters: List<DiscoveryFilter> = emptyList()
) {

   fun withSelector(selector: DiscoverySelector): DiscoveryRequest =
      copy(selectors = this.selectors + selector)

   fun withFilter(filter: DiscoveryFilter): DiscoveryRequest =
      copy(filters = this.filters + filter)

   fun withSelectors(selectors: List<DiscoverySelector>): DiscoveryRequest =
      copy(selectors = this.selectors + selectors)

   fun withFilters(filters: List<DiscoveryFilter>): DiscoveryRequest =
      copy(filters = this.filters + filters)
}

data class FullyQualifiedClassName(val value: String)
data class PackageName(val value: String)

enum class Modifier {
   Public, Internal, Private
}
