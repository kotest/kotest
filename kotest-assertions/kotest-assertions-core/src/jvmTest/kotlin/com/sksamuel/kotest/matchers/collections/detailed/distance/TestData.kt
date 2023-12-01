package com.sksamuel.kotest.matchers.collections.detailed.distance


data class Thing(
    val color: String,
    val shape: String,
)

val redCircle = Thing("red", "circle")
val blueCircle = Thing("blue", "circle")
val redTriangle = Thing("red", "triangle")
val blueTriangle = Thing("blue", "triangle")

data class OtherThing(
    val color: String,
    val shape: String,
)

val otherRedCircle = OtherThing("red", "circle")

data class ThingWithPrivateField(
    val color: String,
    private val shape: String,
)

val redCircleWithPrivateField = ThingWithPrivateField("red", "circle")

data class CountedName(
    val name: String,
    val count: Int
)

val oneApple = CountedName("apple", 1)
val twoApples = CountedName("apple", 2)
val oneOrange = CountedName("orange", 1)
val twoOranges = CountedName("orange", 2)
val threeLemons = CountedName("lemon", 3)
