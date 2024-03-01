package com.sksamuel.kotest.matchers.collections

internal data class Fruit(
   val name: String,
   val color: String,
   val taste: String
)

internal val sweetGreenApple = Fruit("apple", "green", "sweet")
internal val sweetRedApple = Fruit("apple", "red", "sweet")
internal val sweetGreenPear = Fruit("pear", "green", "sweet")
internal val sourYellowLemon = Fruit("lemon", "yellow", "sour")
internal val tartRedCherry = Fruit("cherry", "red", "tart")
internal val bitterPurplePlum = Fruit("plum", "purple", "bitter")
