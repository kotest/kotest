package io.kotest.matchers.floats

import io.kotest.matchers.numeric.ToleranceMatcher

infix fun Float.plusOrMinus(tolerance: Float): FloatToleranceMatcher = FloatToleranceMatcher(this, tolerance)

class FloatToleranceMatcher(expected: Float, tolerance: Float) : ToleranceMatcher<Float>(expected, tolerance.toDouble())
