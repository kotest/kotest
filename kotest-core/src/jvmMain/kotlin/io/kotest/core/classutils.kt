package io.kotest.core

import java.lang.reflect.Modifier

fun Class<*>.superclasses(): List<Class<*>> = generateSequence(this) {
   it.superclass
}.toList()

fun Class<*>.isAbstract() = Modifier.isAbstract(this.modifiers)
fun Class<*>.isConcrete() = !this.isAbstract()
fun Class<*>.isPublic() = Modifier.isPublic(this.modifiers)

fun Class<*>.isSubclassOf(parent: Class<*>) =
   superclasses().map { it.canonicalName }.contains(parent.canonicalName)
