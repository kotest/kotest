package com.sksamuel.kotest.engine.extensions

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.extensions.DescriptorFilterResult
import io.kotest.engine.extensions.ProvidedDescriptorFilter
import io.kotest.matchers.shouldBe

class ProvidedDescriptorFilterTest : FunSpec() {
   init {

      test("filter should exclude tests in a different spec") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor()).filter(
            Spec2::class.toDescriptor().append("foo")
         ) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("filter should exclude tests in the same spec but with a different root name") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor().append("foo")).filter(
            Spec1::class.toDescriptor().append("bar")
         ) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("filter should exclude tests with the same name but different spec") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor().append("foo")).filter(
            Spec2::class.toDescriptor().append("foo")
         ) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("filter should exclude tests in the same spec with the same parent but different name") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor().append("foo").append("bar")).filter(
            Spec1::class.toDescriptor().append("foo").append("baz")
         ) shouldBe DescriptorFilterResult.Exclude(null)
      }

      test("filter should include tests in a matching spec") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor()).filter(
            Spec1::class.toDescriptor().append("foo")
         ) shouldBe DescriptorFilterResult.Include
      }

      test("filter should include tests matching name and spec") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor().append("foo")).filter(
            Spec1::class.toDescriptor().append("foo")
         ) shouldBe DescriptorFilterResult.Include
      }

      test("filter should include nested tests of the accept list") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor().append("foo")).filter(
            Spec1::class.toDescriptor().append("foo").append("bar")
         ) shouldBe DescriptorFilterResult.Include
      }

     test("filter should include parent tests of the accept list") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor().append("foo").append("bar").append("baz")).filter(
            Spec1::class.toDescriptor().append("foo")
         ) shouldBe DescriptorFilterResult.Include
      }

      test("filter should include specs of the accept list") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor().append("foo").append("bar")).filter(
            Spec1::class.toDescriptor()
         ) shouldBe DescriptorFilterResult.Include
      }

      test("filter should support deep descriptors") {
         ProvidedDescriptorFilter(Spec1::class.toDescriptor().append("foo").append("bar").append("baz")).filter(
            Spec1::class.toDescriptor().append("foo").append("bar").append("baz")
         ) shouldBe DescriptorFilterResult.Include
      }


//
//      test("filter should include the target spec") {
//         ProvidedDescriptorFilter(
//            "foo -- bar",
//            Spec1::class
//         ).filter(Spec1::class.toDescriptor()) shouldBe DescriptorFilterResult.Include
//      }
//
//      test("filter should exclude another spec with same test name") {
//         ProvidedDescriptorFilter(
//            "foo -- bar",
//            Spec1::class
//         ).filter(Spec2::class.toDescriptor()) shouldBe DescriptorFilterResult.Exclude("Excluded by test path filter: 'foo -- bar'")
//      }

//      test("filter should work for word spec with when") {
//
//         ProvidedDescriptorFilter("a when", WordSpec2::class).filter(
//            WordSpec2::class.toDescriptor().append("a when")
//         ) shouldBe DescriptorFilterResult.Include
//
//         ProvidedDescriptorFilter("a when -- a should", WordSpec2::class).filter(
//            WordSpec2::class.toDescriptor().append("a when").append("a should")
//         ) shouldBe DescriptorFilterResult.Include
//
//         ProvidedDescriptorFilter("a when -- a should", WordSpec2::class).filter(
//            WordSpec2::class.toDescriptor().append("a when").append("a shouldnt")
//         ) shouldBe DescriptorFilterResult.Exclude("Excluded by test path filter: 'a when -- a should'")
//
//         ProvidedDescriptorFilter("a when -- a should -- a test", WordSpec2::class).filter(
//            WordSpec2::class.toDescriptor().append("a when").append("a should").append("a test")
//         ) shouldBe DescriptorFilterResult.Include
//
//         ProvidedDescriptorFilter("a when -- a should -- a test", WordSpec2::class).filter(
//            WordSpec2::class.toDescriptor().append("a when").append("a should").append("boo")
//         ) shouldBe DescriptorFilterResult.Exclude("Excluded by test path filter: 'a when -- a should -- a test'")
//      }
//

   }
}

private class Spec1 : StringSpec() {
   init {
      "foo" {}
      "boo" {}
   }
}

private class Spec2 : StringSpec() {
   init {
      "foo" {}
      "boo" {}
   }
}

private class WordSpec1 : WordSpec() {
   init {
      "a container" should {
         "skip a test".config(enabled = false) {}
         "pass a test" { 1 shouldBe 1 }
      }
   }
}

private class WordSpec2 : WordSpec() {
   init {
      "a when" When {
         "a should" should {
            "a test" { }
         }
      }
   }
}
