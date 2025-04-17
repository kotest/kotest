//package io.kotest.engine.launcher
//
//import io.kotest.core.descriptors.Descriptor
//import io.kotest.core.descriptors.Descriptor.Companion.TestDelimiter
//import io.kotest.core.spec.Spec
//import io.kotest.engine.descriptors.toDescriptor
//import io.kotest.engine.extensions.DescriptorFilter
//import io.kotest.engine.extensions.TestFilterResult
//import kotlin.reflect.KClass
//
///**
// * Compares test descriptions to a given test path (delimited with ' -- ').
// * The comparison ignores test prefixes, so an application using the launcher should not
// * include test name prefixes in the test path.
// */
//class TestPathTestCaseFilter(
//   private val testPath: String,
//   spec: KClass<out Spec>,
//) : DescriptorFilter {
//
//   private val target1 = testPath.trim().split(TestDelimiter)
//      .fold(spec.toDescriptor() as Descriptor) { desc, name ->
//         desc.append(name.trim())
//      }
//
//   // this is a hack where we append "should" to the first name, until 5.0 where we will
//   // store names with affixes separately (right now word spec is adding them to the names at source)
//   var should = true
//   private val target2 = testPath.trim().split(TestDelimiter)
//      .fold(spec.toDescriptor() as Descriptor) { desc, name ->
//         if (should) {
//            should = false
//            desc.append("$name should")
//         } else desc.append(name.trim())
//      }
//
//
//   private fun List<Descriptor>.prefixesWithWildcardMatch(that: List<Descriptor>): Boolean {
//      if (this.isEmpty()) {
//         return true
//      }
//      if (this.size > that.size) {
//         return false
//      }
//
//      val first = this[0]
//      val second = that[0]
//      if (!first.isEqualType(second)) {
//         return false
//      }
//      return first.id.wildCardMatch(second.id) && this.subList(1, this.size).prefixesWithWildcardMatch(that.subList(1, that.size))
//   }
//
//   /**
//    * This filter is called in a tree like manner so there are two cases possible
//    * first we check if the group we are currently running is parent of the test filter.
//    * Returning true at this point means that test engine will keep on going recursively deeper into the
//    * hierarchy
//    * Eventually we are deep into the hierarchy where we have runnable targets that will be on the
//    * path of the filters which means they can be run
//    */
//   override fun filter(descriptor: Descriptor): TestFilterResult {
//      val descriptorPrefix = descriptor.getTreePrefix()
//      val target1Prefix = target1.getTreePrefix()
//      val target2Prefix = target2.getTreePrefix()
//      val onTestFilterPath = descriptorPrefix.prefixesWithWildcardMatch(target1Prefix)
//         || descriptorPrefix.prefixesWithWildcardMatch(target2Prefix)
//      val testOnDescriptorPath = target1Prefix.prefixesWithWildcardMatch(descriptorPrefix)
//         || target2Prefix.prefixesWithWildcardMatch(descriptorPrefix)
//      return when {
//         onTestFilterPath || testOnDescriptorPath -> TestFilterResult.Include
//         else -> TestFilterResult.Exclude("Excluded by test path filter: '$testPath'")
//      }
//   }
//}
