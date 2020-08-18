package com.sksamuel.kotest.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.internal.tags.isActive
import io.kotest.core.internal.tags.parse
import io.kotest.matchers.shouldBe

class TagsTest : StringSpec() {

   object Moo : Tag()
   object Foo : Tag()
   object Roo : Tag()

   init {
      "test with include and exclude tags" {
         val tags = Tags(setOf(Foo, NamedTag("boo")), setOf(Moo))
         tags.parse().isActive(Foo) shouldBe true
         tags.parse().isActive(NamedTag("boo")) shouldBe true
         tags.parse().isActive(Moo) shouldBe false // moo excluded
         tags.parse().isActive(NamedTag("goo")) shouldBe false // missing any of the included
         tags.parse().isActive(Roo) shouldBe false  // missing any of the included
         tags.parse().isActive(setOf(Moo, Roo)) shouldBe false // moo excluded
         tags.parse().isActive(setOf(Moo, Foo)) shouldBe false // moo excluded
         tags.parse().isActive(setOf(NamedTag("boo"), Foo)) shouldBe true // has both the included
      }
      "test with include tags" {
         val tags = Tags(setOf(Foo, NamedTag("boo")), emptySet())
         tags.parse().isActive(Foo) shouldBe true
         tags.parse().isActive(NamedTag("boo")) shouldBe true
         tags.parse().isActive(Moo) shouldBe false
         tags.parse().isActive(NamedTag("goo")) shouldBe false
         tags.parse().isActive(Roo) shouldBe false
      }
      "test with exclude tags" {
         val tags = Tags(emptySet(), setOf(Moo))
         tags.parse().isActive(Foo) shouldBe true
         tags.parse().isActive(NamedTag("boo")) shouldBe true
         tags.parse().isActive(Moo) shouldBe false
         tags.parse().isActive(NamedTag("goo")) shouldBe true
         tags.parse().isActive(Roo) shouldBe true
      }
      "test with no tags" {
         val tags = Tags(emptySet(), emptySet())
         tags.parse().isActive(Foo) shouldBe true
         tags.parse().isActive(NamedTag("boo")) shouldBe true
         tags.parse().isActive(Moo) shouldBe true
         tags.parse().isActive(NamedTag("goo")) shouldBe true
         tags.parse().isActive(Roo) shouldBe true
      }
      "test with simple expression" {
         val tags = Tags("Foo")
         tags.parse().isActive(Foo) shouldBe true
         tags.parse().isActive(Roo) shouldBe false
         tags.parse().isActive(Moo) shouldBe false
         tags.parse().isActive(setOf(Foo, Roo)) shouldBe true
         tags.parse().isActive(setOf(Foo, Moo, Roo)) shouldBe true
      }
      "test with or expression" {
         val tags = Tags("Foo | Roo")
         tags.parse().isActive(Foo) shouldBe true
         tags.parse().isActive(Roo) shouldBe true
         tags.parse().isActive(Moo) shouldBe false
         tags.parse().isActive(setOf(Foo, Roo)) shouldBe true
         tags.parse().isActive(setOf(Foo, Moo, Roo)) shouldBe true
      }
      "test with and expression" {
         val tags = Tags("Foo & Roo")
         tags.parse().isActive(Foo) shouldBe false
         tags.parse().isActive(Roo) shouldBe false
         tags.parse().isActive(Moo) shouldBe false
         tags.parse().isActive(setOf(Foo, Roo)) shouldBe true
         tags.parse().isActive(setOf(Foo, Moo, Roo)) shouldBe true
         tags.parse().isActive(setOf(Foo, Moo)) shouldBe false
      }
      "test with not expression" {
         val tags = Tags("!Roo")
         tags.parse().isActive(Foo) shouldBe true
         tags.parse().isActive(Roo) shouldBe false
         tags.parse().isActive(Moo) shouldBe true
         tags.parse().isActive(setOf(Foo, Roo)) shouldBe false
         tags.parse().isActive(setOf(Foo, Moo, Roo)) shouldBe false
         tags.parse().isActive(setOf(Foo, Moo)) shouldBe true
      }
      "test with not expression and join" {
         val tags = Tags("!Roo & Foo")
         tags.parse().isActive(Foo) shouldBe true
         tags.parse().isActive(Roo) shouldBe false // roo excluded
         tags.parse().isActive(Moo) shouldBe false // missing foo
         tags.parse().isActive(setOf(Foo, Roo)) shouldBe false
         tags.parse().isActive(setOf(Foo, Moo, Roo)) shouldBe false
         tags.parse().isActive(setOf(Foo, Moo)) shouldBe true
         tags.parse().isActive(setOf(Moo, Roo)) shouldBe false
      }
      "test with parens" {
         val tags = Tags("(Roo | Foo) & Moo")
         tags.parse().isActive(Foo) shouldBe false // missing Moo
         tags.parse().isActive(Roo) shouldBe false // missing Moo
         tags.parse().isActive(Moo) shouldBe false // missing Roo | Foo
         tags.parse().isActive(setOf(Roo, Moo)) shouldBe true
         tags.parse().isActive(setOf(Foo, Moo)) shouldBe true // foo is excluded
         tags.parse().isActive(setOf(Foo, Moo, Roo)) shouldBe true
      }
      "test with not on parens" {
         val tags = Tags("!(Roo | Foo) & Moo")
         tags.parse().isActive(Foo) shouldBe false // Foo excluded
         tags.parse().isActive(Roo) shouldBe false // Roo excluded
         tags.parse().isActive(Moo) shouldBe true
         tags.parse().isActive(setOf(Roo, Moo)) shouldBe false
         tags.parse().isActive(setOf(Foo, Moo)) shouldBe false
         tags.parse().isActive(setOf(Foo, Moo, Roo)) shouldBe false
      }
   }
}
