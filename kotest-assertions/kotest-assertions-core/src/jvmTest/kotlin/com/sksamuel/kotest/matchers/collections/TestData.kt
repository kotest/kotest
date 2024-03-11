package com.sksamuel.kotest.matchers.collections

data class Fruit(
   val name: String,
   val color: String,
   val taste: String
)

val sweetGreenApple = Fruit("apple", "green", "sweet")
val sweetRedApple = Fruit("apple", "red", "sweet")
val sweetGreenPear = Fruit("pear", "green", "sweet")
val sourYellowLemon = Fruit("lemon", "yellow", "sour")
val tartRedCherry = Fruit("cherry", "red", "tart")
val bitterPurplePlum = Fruit("plum", "purple", "bitter")
