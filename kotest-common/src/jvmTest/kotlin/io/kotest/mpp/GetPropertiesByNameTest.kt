package io.kotest.mpp

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class GetPropertiesByNameTest: WordSpec() {
   init {
       "getPropertiesByName" should {
          "return properties when there are no functions with same name" {
             JvmReflection.getPropertiesByName(Fruit::class).keys shouldBe setOf("name" , "weight")
          }
          "handle case when field and function have the same name" {
             val actual = JvmReflection.getPropertiesByName(FruitWithMemberNameCollision::class)["weight"]
             val fruitWithMemberNameCollision = FruitWithMemberNameCollision("apple", 12)
             withClue("Guardian assumption: function and field return different values") {
                fruitWithMemberNameCollision.weight() shouldNotBe fruitWithMemberNameCollision.weight
             }
             actual!!.call(fruitWithMemberNameCollision) shouldBe fruitWithMemberNameCollision.weight
          }
       }
      "primaryConstructorMembers" should {
         "return properties when there are no functions with same name" {
            val fruit = Fruit("apple", 12)
            JvmReflection.primaryConstructorMembers(Fruit::class).map {
               it.call(fruit)
            } shouldContainExactlyInAnyOrder listOf(fruit.name, fruit.weight)
         }
         "handle case when field and function have the same name" {
            val actual = JvmReflection.primaryConstructorMembers(FruitWithMemberNameCollision::class)
            val fruitWithMemberNameCollision = FruitWithMemberNameCollision("apple", 12)
            withClue("Guardian assumption: function and field return different values") {
               fruitWithMemberNameCollision.weight() shouldNotBe fruitWithMemberNameCollision.weight
            }
            actual.map {
               it.call(fruitWithMemberNameCollision)
            } shouldContainExactlyInAnyOrder listOf(
               fruitWithMemberNameCollision.name,
               fruitWithMemberNameCollision.weight
            )
         }
      }
   }

   data class Fruit(
      val name: String,
      val weight: Int
   )
   data class FruitWithMemberNameCollision(
      val name: String,
      val weight: Int
   ) {
      fun weight() = 42
   }
}
