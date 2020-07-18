package com.sksamuel.kotest.tags

import io.kotest.core.StringTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec

class TagsTest : StringSpec() {

   object Moo : Tag()
   object Foo : Tag()
   object Roo : Tag()

   init {
      "test with include and exclude tags" {
         val tags = Tags(setOf(Foo, StringTag("boo")), setOf(Moo))
         tags.isActive(Foo) shouldBe true
         tags.isActive(StringTag("boo")) shouldBe true
         tags.isActive(Moo) shouldBe false // moo excluded
         tags.isActive(StringTag("goo")) shouldBe false // missing any of the included
         tags.isActive(Roo) shouldBe false  // missing any of the included
         tags.isActive(setOf(Moo, Roo)) shouldBe false // moo excluded
         tags.isActive(setOf(Moo, Foo)) shouldBe false // moo excluded
         tags.isActive(setOf(StringTag("boo"), Foo)) shouldBe true // has both the included
      }
      "test with include tags" {
         val tags = Tags(setOf(Foo, StringTag("boo")), emptySet())
         tags.isActive(Foo) shouldBe true
         tags.isActive(StringTag("boo")) shouldBe true
         tags.isActive(Moo) shouldBe false
         tags.isActive(StringTag("goo")) shouldBe false
         tags.isActive(Roo) shouldBe false
      }
      "test with exclude tags" {
         val tags = Tags(emptySet(), setOf(Moo))
         tags.isActive(Foo) shouldBe true
         tags.isActive(StringTag("boo")) shouldBe true
         tags.isActive(Moo) shouldBe false
         tags.isActive(StringTag("goo")) shouldBe true
         tags.isActive(Roo) shouldBe true
      }
      "test with no tags" {
         val tags = Tags(emptySet(), emptySet())
         tags.isActive(Foo) shouldBe true
         tags.isActive(StringTag("boo")) shouldBe true
         tags.isActive(Moo) shouldBe true
         tags.isActive(StringTag("goo")) shouldBe true
         tags.isActive(Roo) shouldBe true
      }
      "test with simple expression" {
         val tags = Tags("Foo")
         tags.isActive(Foo) shouldBe true
         tags.isActive(Roo) shouldBe false
         tags.isActive(Moo) shouldBe false
         tags.isActive(setOf(Foo, Roo)) shouldBe true
         tags.isActive(setOf(Foo, Moo, Roo)) shouldBe true
      }
      "test with or expression" {
         val tags = Tags("Foo | Roo")
         tags.isActive(Foo) shouldBe true
         tags.isActive(Roo) shouldBe true
         tags.isActive(Moo) shouldBe false
         tags.isActive(setOf(Foo, Roo)) shouldBe true
         tags.isActive(setOf(Foo, Moo, Roo)) shouldBe true
      }
      "test with and expression" {
         val tags = Tags("Foo & Roo")
         tags.isActive(Foo) shouldBe false
         tags.isActive(Roo) shouldBe false
         tags.isActive(Moo) shouldBe false
         tags.isActive(setOf(Foo, Roo)) shouldBe true
         tags.isActive(setOf(Foo, Moo, Roo)) shouldBe true
         tags.isActive(setOf(Foo, Moo)) shouldBe false
      }
      "test with not expression" {
         val tags = Tags("!Roo")
         tags.isActive(Foo) shouldBe true
         tags.isActive(Roo) shouldBe false
         tags.isActive(Moo) shouldBe true
         tags.isActive(setOf(Foo, Roo)) shouldBe false
         tags.isActive(setOf(Foo, Moo, Roo)) shouldBe false
         tags.isActive(setOf(Foo, Moo)) shouldBe true
      }
      "test with not expression and join" {
         val tags = Tags("!Roo & Foo")
         tags.isActive(Foo) shouldBe true
         tags.isActive(Roo) shouldBe false // roo excluded
         tags.isActive(Moo) shouldBe false // missing foo
         tags.isActive(setOf(Foo, Roo)) shouldBe false
         tags.isActive(setOf(Foo, Moo, Roo)) shouldBe false
         tags.isActive(setOf(Foo, Moo)) shouldBe true
         tags.isActive(setOf(Moo, Roo)) shouldBe false
      }
      "test with parens" {
         val tags = Tags("(Roo | Foo) & Moo")
         tags.isActive(Foo) shouldBe false // missing Moo
         tags.isActive(Roo) shouldBe false // missing Moo
         tags.isActive(Moo) shouldBe false // missing Roo | Foo
         tags.isActive(setOf(Roo, Moo)) shouldBe true
         tags.isActive(setOf(Foo, Moo)) shouldBe true // foo is excluded
         tags.isActive(setOf(Foo, Moo, Roo)) shouldBe true
      }
      "test with not on parens" {
         val tags = Tags("!(Roo | Foo) & Moo")
         tags.isActive(Foo) shouldBe false // Foo excluded
         tags.isActive(Roo) shouldBe false // Roo excluded
         tags.isActive(Moo) shouldBe true
         tags.isActive(setOf(Roo, Moo)) shouldBe false
         tags.isActive(setOf(Foo, Moo)) shouldBe false
         tags.isActive(setOf(Foo, Moo, Roo)) shouldBe false
      }
   }
}
