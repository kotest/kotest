package com.sksamuel.kotest.engine.descriptors

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class DescriptorTest : FunSpec({

   // build up descriptors for :
   // DescriptorTest/a -- b -- c
   // DescriptorTest/d -- e
   // DescriptorTest/f
   val spec = DescriptorTest::class.toDescriptor()
   val jsSpec = Descriptor.SpecDescriptor(DescriptorId("DescriptorTest"))

   val a = spec.append("a")
   val b = a.append("b")
   val c = b.append("c")
   val d = spec.append("d")
   val e = d.append("e")
   val f = spec.append("f")

   // descriptor tree rooted at jsSpec (simple name, no FQN) for "without FQNs" tests
   val jsA = jsSpec.append("a")
   val jsB = jsA.append("b")
   val jsC = jsB.append("c")
   val jsD = jsSpec.append("d")
   val jsE = jsD.append("e")
   val jsF = jsSpec.append("f")

   test("isTestCase") {
      spec.isTestCase() shouldBe false
      listOf(a, b, c, d, e, f).forAll {
         it.isTestCase() shouldBe true
      }
   }

   test("isRoot") {
      listOf(a, d, f).forAll {
         it.isRootTest() shouldBe true
      }
      listOf(spec, b, c, e).forAll {
         it.isRootTest() shouldBe false
      }
   }

   test("isChildOf") {

      a.isChildOf(b) shouldBe false
      b.isChildOf(a) shouldBe true

      c.isChildOf(b) shouldBe true
      b.isChildOf(c) shouldBe false

      d.isChildOf(e) shouldBe false

      spec.isChildOf(a) shouldBe false
      a.isChildOf(spec) shouldBe true
      d.isChildOf(spec) shouldBe true
      f.isChildOf(spec) shouldBe true
   }

   test("isParentOf") {
      a.isParentOf(b) shouldBe true
      b.isParentOf(a) shouldBe false
      a.isParentOf(c) shouldBe false

      b.isParentOf(c) shouldBe true
      c.isParentOf(b) shouldBe false

      d.isParentOf(e) shouldBe true
      e.isParentOf(d) shouldBe false

      spec.isParentOf(a) shouldBe true
      a.isParentOf(spec) shouldBe false
      d.isParentOf(spec) shouldBe false
      f.isParentOf(spec) shouldBe false
   }

   test("isAncestorOf") {
      a.isAncestorOf(b) shouldBe true
      b.isAncestorOf(a) shouldBe false

      c.isAncestorOf(b) shouldBe false
      b.isAncestorOf(c) shouldBe true
      a.isAncestorOf(c) shouldBe true
      a.isAncestorOf(b) shouldBe true

      d.isAncestorOf(e) shouldBe true
      e.isAncestorOf(d) shouldBe false

      listOf(a, b, c, d, e, f).forAll {
         it.isAncestorOf(spec) shouldBe false
         spec.isAncestorOf(it) shouldBe true
      }
   }

   test("isDescendentOf") {

      a.isDescendentOf(b) shouldBe false
      b.isDescendentOf(c) shouldBe false
      b.isDescendentOf(a) shouldBe true
      c.isDescendentOf(a) shouldBe true
      c.isDescendentOf(b) shouldBe true

      e.isDescendentOf(d) shouldBe true
      d.isDescendentOf(e) shouldBe false

      listOf(a, b, c, d, e, f).forAll {
         it.isDescendentOf(spec) shouldBe true
         spec.isDescendentOf(it) shouldBe false
      }
   }

   test("isDescendentOf without FQNs") {

      jsA.isDescendentOf(jsB) shouldBe false
      jsB.isDescendentOf(jsC) shouldBe false
      jsB.isDescendentOf(jsA) shouldBe true
      jsC.isDescendentOf(jsA) shouldBe true
      jsC.isDescendentOf(jsB) shouldBe true

      jsE.isDescendentOf(jsD) shouldBe true
      jsD.isDescendentOf(jsE) shouldBe false

      listOf(jsA, jsB, jsC, jsD, jsE, jsF).forAll {
         it.isDescendentOf(jsSpec) shouldBe true
         jsSpec.isDescendentOf(it) shouldBe false
      }
   }

   test("isPrefixOf") {
      spec.isPrefixOf(a) shouldBe true
      spec.isPrefixOf(b) shouldBe true
      spec.isPrefixOf(c) shouldBe true
      a.isPrefixOf(b) shouldBe true
      a.isPrefixOf(c) shouldBe true
      b.isPrefixOf(a) shouldBe false
      c.isPrefixOf(a) shouldBe false
      c.isPrefixOf(b) shouldBe false

      d.isPrefixOf(e) shouldBe true
      e.isPrefixOf(d) shouldBe false
   }

   test("isPrefixOf without FQNs") {
      jsSpec.isPrefixOf(jsA) shouldBe true
      jsSpec.isPrefixOf(jsB) shouldBe true
      jsSpec.isPrefixOf(jsC) shouldBe true
      jsA.isPrefixOf(jsB) shouldBe true
      jsA.isPrefixOf(jsC) shouldBe true
      jsB.isPrefixOf(jsA) shouldBe false
      jsC.isPrefixOf(jsA) shouldBe false
      jsC.isPrefixOf(jsB) shouldBe false

      jsD.isPrefixOf(jsE) shouldBe true
      jsE.isPrefixOf(jsD) shouldBe false
   }

   test("hasSharedPath") {

      listOf(a, b, c, d, e, f).forAll {
         spec.hasSharedPath(it) shouldBe true
      }

      a.hasSharedPath(b) shouldBe true
      a.hasSharedPath(c) shouldBe true
      b.hasSharedPath(a) shouldBe true
      b.hasSharedPath(c) shouldBe true
      c.hasSharedPath(a) shouldBe true
      c.hasSharedPath(b) shouldBe true

      d.hasSharedPath(e) shouldBe true
      e.hasSharedPath(d) shouldBe true

      f.hasSharedPath(a) shouldBe false
      f.hasSharedPath(b) shouldBe false
      f.hasSharedPath(c) shouldBe false
   }

   test("hasSharedPath without FQNs") {

      listOf(jsA, jsB, jsC, jsD, jsE, jsF).forAll {
         jsSpec.hasSharedPath(it) shouldBe true
      }

      jsA.hasSharedPath(jsB) shouldBe true
      jsA.hasSharedPath(jsC) shouldBe true
      jsB.hasSharedPath(jsA) shouldBe true
      jsB.hasSharedPath(jsC) shouldBe true
      jsC.hasSharedPath(jsA) shouldBe true
      jsC.hasSharedPath(jsB) shouldBe true

      jsD.hasSharedPath(jsE) shouldBe true
      jsE.hasSharedPath(jsD) shouldBe true

      jsF.hasSharedPath(jsA) shouldBe false
      jsF.hasSharedPath(jsB) shouldBe false
      jsF.hasSharedPath(jsC) shouldBe false
   }
})
