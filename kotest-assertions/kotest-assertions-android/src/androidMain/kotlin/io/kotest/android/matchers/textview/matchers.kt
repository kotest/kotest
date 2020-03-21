package io.kotest.android.matchers.textview

import android.content.res.Resources.Theme
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot

infix fun TextView.shouldHaveText(text: String) = this should haveText(text)
infix fun TextView.shouldNotHaveText(text: String) = this shouldNot haveText(text)

fun haveText(text: String) = object : Matcher<TextView> {
    override fun test(value: TextView) = MatcherResult(
        text.contentEquals(value.text),
        "TextView should have text $text but was $value.text",
        "TextView should not have text $text, but had"
    )
    
}

infix fun TextView.shouldHaveTextColorId(@ColorRes colorId: Int) = this.shouldHaveTextColorId(colorId, null)
fun TextView.shouldHaveTextColorId(@ColorRes colorId: Int, theme: Theme? = null) = if (VERSION.SDK_INT >= VERSION_CODES.M) {
    this.shouldHaveTextColor(resources.getColor(colorId, theme))
} else {
    this.shouldHaveTextColor(resources.getColor(colorId))
}

infix fun TextView.shouldNotHaveTextColorId(@ColorRes colorId: Int) = this.shouldNotHaveTextColorId(colorId, null)
fun TextView.shouldNotHaveTextColorId(@ColorRes colorId: Int, theme: Theme? = null) = if (VERSION.SDK_INT >= VERSION_CODES.M) {
    this.shouldNotHaveTextColor(resources.getColor(colorId, theme))
} else {
    this.shouldNotHaveTextColor(resources.getColor(colorId))
}


infix fun TextView.shouldHaveTextColor(@ColorInt color: Int) = this should haveTextColor(color)
infix fun TextView.shouldNotHaveTextColor(@ColorInt color: Int) = this shouldNot haveTextColor(color)

fun haveTextColor(@ColorInt color: Int) = object: Matcher<TextView> {
    override fun test(value: TextView): MatcherResult {
        val currentColor = value.currentTextColor
        
        return MatcherResult(
            color == currentColor,
            "TextView should have color $color but was $currentColor",
            "TextView should not have color $color, but had"
        )
    }
    
}

@RequiresApi(VERSION_CODES.P)
fun TextView.shouldBeAllCaps() = this should beAllCaps()

@RequiresApi(VERSION_CODES.P)
fun TextView.shouldNotBeAllCaps() = this shouldNot beAllCaps()

@RequiresApi(VERSION_CODES.P)
fun beAllCaps() = object : Matcher<TextView> {
    override fun test(value: TextView) = MatcherResult(
        value.isAllCaps,
        "TextView should have AllCaps as transformation method",
        "TextView should not have AllCaps as transformation method"
    )
}

@RequiresApi(VERSION_CODES.JELLY_BEAN_MR1)
infix fun TextView.shouldHaveTextAlignment(alignment: Int) = this should haveTextAlignment(alignment)

@RequiresApi(VERSION_CODES.JELLY_BEAN_MR1)
infix fun TextView.shouldNotHaveTextAlignment(alignment: Int) = this shouldNot haveTextAlignment(alignment)

@RequiresApi(VERSION_CODES.JELLY_BEAN_MR1)
fun haveTextAlignment(alignment: Int) = object : Matcher<TextView> {
    override fun test(value: TextView) = MatcherResult(
        value.textAlignment == alignment,
        "TextView should have text alignment $alignment but was ${value.textAlignment}",
        "TextView should not have text alignment $alignment, but had"
    )
}