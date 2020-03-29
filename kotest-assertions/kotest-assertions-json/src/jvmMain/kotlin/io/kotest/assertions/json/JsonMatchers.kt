package io.kotest.assertions.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import io.kotest.assertions.failure
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

@OptIn(ExperimentalContracts::class)
infix fun Json?.shouldMatchJson(json: Json?) {
    contract {
        returns() implies (this@shouldMatchJson != null)
    }

    this should matchJson(json)
}

@OptIn(ExperimentalContracts::class)
infix fun Json?.shouldNotMatchJson(json: Json?) {
    contract {
        returns() implies (this@shouldNotMatchJson != null)
    }

    this shouldNot matchJson(json)
}

fun matchJson(json: Json?) = object : Matcher<Json?> {

    override fun test(value: Json?): MatcherResult {
        val actualJson = value?.let { mapper.readTree(it) }
        val expectedJson = json?.let { mapper.readTree(it) }

        return MatcherResult(
            actualJson == expectedJson,
            "expected: $expectedJson but was: $actualJson",
            "expected not to match with: $expectedJson but match: $actualJson"
        )
    }
}

infix fun Json.shouldMatchJsonResource(resource: String) = this should matchJsonResource(resource)
infix fun Json.shouldNotMatchJsonResource(resource: String) = this shouldNot matchJsonResource(resource)

fun matchJsonResource(resource: String) = object : Matcher<Json> {

    override fun test(value: Json): MatcherResult {
        val actualJson = mapper.readTree(value)
        val expectedJson = mapper.readTree(this.javaClass.getResourceAsStream(resource))

        return MatcherResult(
            actualJson == expectedJson,
            "expected: $expectedJson but was: $actualJson",
            "expected not to match with: $expectedJson but match: $actualJson"
        )
    }
}

@OptIn(ExperimentalContracts::class)
infix fun Json?.shouldContainJsonKey(path: JsonKey): Json? {
    contract {
        returns() implies (this@shouldContainJsonKey != null)
    }

    val result: Any? = try {
        JsonPath.read(this, path)
    } catch (thrown: Throwable) {
        thrown
    }

    return when (result) {
        is Throwable -> throw failure(
            "${this?.limitLength()?.representation} should contain the path $path",
            cause = result
        )

        else -> result?.let { mapper.writeValueAsString(it) }
    }
}

infix fun Json.shouldNotContainJsonKey(path: JsonKey) = this shouldNot containJsonKey(path)
fun containJsonKey(path: JsonKey) = object : Matcher<Json> {

    override fun test(value: Json): MatcherResult {
        val sub = value.limitLength()

        val passed = try {
            JsonPath.read<String>(value, path) != null
        } catch (t: PathNotFoundException) {
            false
        }

        return MatcherResult(
            passed,
            "${sub.representation} should contain the path $path",
            "${sub.representation} should not contain the path $path"
        )
    }
}

fun <T> Json?.shouldContainJsonKeyValue(path: JsonKey, value: T) = this should containJsonKeyValue(path, value)
fun <T> Json?.shouldNotContainJsonKeyValue(path: JsonKey, value: T) = this shouldNot containJsonKeyValue(path, value)
fun <T> containJsonKeyValue(path: JsonKey, t: T) = object : Matcher<Json?> {
    override fun test(value: Json?): MatcherResult {
        val sub = value?.limitLength()

        val result: Any? = try {
            JsonPath.read<T>(value, path)
        } catch (thrown: Throwable) {
            thrown
        }

        return MatcherResult(
            result == t,
            "${sub.representation} should contain the element $path = $t",
            "${sub.representation} should not contain the element $path = $t"
        )
    }
}

infix fun Json?.shouldContainExactly(countableElement: JsonCountableElement) =
    this should containExactly(countableElement)

infix fun Json?.shouldNotContainExactly(countableElement: JsonCountableElement) =
    this shouldNot containExactly(countableElement)

fun containExactly(countableElement: JsonCountableElement) = object : Matcher<Json?> {

    override fun test(value: Json?): MatcherResult = when (countableElement) {
        is JsonCountableElement.JsonKeyValuePairs -> {
            // todo: check if it's a JSON object and not an array

            val expected = countableElement.count
            val actual: Int? = value?.let { JsonPath.read(it, "length()") }

            MatcherResult(
                expected == actual,
                "bad quantity of key-value pairs in ${value.representation} - expected: $expected but was: $actual",
                "quantity of key-value pairs in ${value.representation} - " +
                        "expected not to match with: $expected but match: $actual"
            )
        }
    }
}

@OptIn(ExperimentalContracts::class, ExperimentalStdlibApi::class)
inline infix fun <reified T> Json?.shouldContainJsonKeyAndValueOfSpecificType(path: JsonKey): T {
    contract {
        returns() implies (this@shouldContainJsonKeyAndValueOfSpecificType != null)
    }

    val result: Any? = try {
        JsonPath.read(this, path)
    } catch (thrown: Throwable) {
        thrown
    }

    return when (result) {
        is T -> result

        is Throwable -> throw failure(
            "${this.representation} should contain the path $path",
            cause = result
        )

        else -> throw failure(
            "${this.representation} should contain an element with type ${typeOf<T>()} with the path $path " +
                    "but it contains ${result?.let { mapper.writeValueAsString(it) }.representation}."
        )
    }
}

@OptIn(ExperimentalContracts::class)
infix fun Json?.shouldContainOnlyJsonKey(path: JsonKey): Json? {
    contract {
        returns() implies (this@shouldContainOnlyJsonKey != null)
    }

    this shouldContainExactly 1.jsonKeyValueEntries

    return this shouldContainJsonKey path
}

@OptIn(ExperimentalContracts::class)
inline infix fun <reified T> Json?.shouldContainOnlyJsonKeyAndValueOfSpecificType(path: JsonKey): T {
    contract {
        returns() implies (this@shouldContainOnlyJsonKeyAndValueOfSpecificType != null)
    }

    this shouldContainExactly 1.jsonKeyValueEntries

    return this shouldContainJsonKeyAndValueOfSpecificType path
}
