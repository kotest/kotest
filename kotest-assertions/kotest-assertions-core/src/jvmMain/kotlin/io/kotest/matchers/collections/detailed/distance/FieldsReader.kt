package io.kotest.matchers.collections.detailed.distance

import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

class FieldsReader {
    fun fieldsOf(instance: Any): List<FieldAndValue> {
        require(instance::class.isData) {
            "Data classes expected, was: ${instance::class.qualifiedName}"
        }
        val fields = getFields(instance)
        return fields.asSequence()
            .map {
                it.getter.isAccessible = true
                val value = it.getter.call(instance)
                FieldAndValue(it.name, value)
            }
            .toList()
    }

    fun getFields(instance: Any): Collection<KProperty1<out Any, *>> {
        val fields = instance::class.memberProperties
        val klass = instance.javaClass.kotlin
        val primaryConstructor = klass.primaryConstructor
        val params = primaryConstructor!!.parameters
        return params.map { param -> fields.first { field -> field.name == param.name} }
    }
}

data class FieldAndValue(
    val name: String,
    val value: Any?
)
