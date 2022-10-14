package io.kotest.matchers.numeric

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import kotlin.math.abs

open class ToleranceMatcher<T : Number>(private val expected: T, private val tolerance: Double) : Matcher<T> {
    init {
        require(
            !expected.toDouble().isNaN()
        ) { "By design, NaN != NaN; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776" }

        require(!tolerance.isNaN()) { "Tolerance must be a number" }
        require(tolerance >= 0) { "Tolerance can't be negative" }
        if (tolerance == 0.0) {
            println("[WARN] When comparing Float consider using tolerance, eg: a shouldBe b plusOrMinus c")
        }
    }

    private val minInclusive = expected.toDouble() - tolerance
    private val maxInclusive = expected.toDouble() + tolerance

    init {
        require(!minInclusive.isNaN()) { "Lower bound for comparison can't be NaN" }
        require(!maxInclusive.isNaN()) { "Upper bound for comparison can't be NaN" }
    }

    override fun test(value: T) = MatcherResult(
        value.toDouble() in minInclusive..maxInclusive,
        { "$value should be equal to $expected within tolerance of $tolerance (lowest acceptable value is $minInclusive; highest acceptable value is $maxInclusive)" },
        { "$value should not be equal to $expected within tolerance of $tolerance (value must be higher than $maxInclusive or lower than $minInclusive)" },
    )
}