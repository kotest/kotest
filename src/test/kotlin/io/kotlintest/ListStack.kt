package io.kotlintest

import kotlin.collections.*

class ListStack<T> : Stack<T> {

    var list = listOf<T>()

    override fun peek(): T = list.last()

    override fun pop(): T {
        val t = peek()
        list = list.drop(1)
        return t
    }

    override fun push(t: T): Unit {
        list += t
    }

    override fun size(): Int = list.size

    override fun isEmpty(): Boolean = list.isEmpty()
}