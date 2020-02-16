package io.kotest.android.matchers.view

import android.view.View
import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot

fun View.shouldBeVisible() = this should beVisible()
fun View.shouldNotBeVisible() = this shouldNot beVisible()

fun beVisible() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.visibility == View.VISIBLE,
        "View should be VISIBLE but was ${value.visibilityAsString()}",
        "View should not be VISIBLE, but was"
    )
}

fun View.shouldBeInvisible() = this should beInvisible()
fun View.shouldNotBeInvisible() = this shouldNot beInvisible()

fun beInvisible() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.visibility == View.INVISIBLE,
        "View should be INVISIBLE, but was ${value.visibilityAsString()}",
        "View should not be INVISIBLE, but was"
    )
}

fun View.shouldBeGone() = this should beGone()
fun View.shouldNotBeGone() = this shouldNot beGone()

fun beGone() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.visibility == View.GONE,
        "View should be GONE, but was ${value.visibilityAsString()}",
        "View should not be GONE, but was"
    )
    
}

private fun View.visibilityAsString() = when (visibility) {
    View.VISIBLE   -> "VISIBLE"
    View.INVISIBLE -> "INVISIBLE"
    View.GONE      -> "GONE"
    else           -> throw IllegalStateException("No more possible visibility values")
}


fun View.shouldHaveContentDescription() = this should haveContentDescription()
fun View.shouldNotHaveContentDescription() = this shouldNot haveContentDescription()

fun haveContentDescription() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.contentDescription != null && value.contentDescription.isNotBlank(),
        "View should have ContentDescription, but had none",
        "View should not have ContentDescription, but had ${value.contentDescription}"
    )
}

infix fun View.shouldHaveContentDescription(contentDescription: String) = this should haveContentDescription(contentDescription)
infix fun View.shouldNotHaveContentDescription(contentDescription: String) = this shouldNot haveContentDescription(contentDescription)

fun haveContentDescription(contentDescription: String) = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        contentDescription.contentEquals(value.contentDescription),
        "View should have ContentDescription $contentDescription, but was ${value.contentDescription}",
        "View should not have ContentDescription $contentDescription, but had"
    )
}

infix fun View.shouldHaveTag(pair: Pair<Int, Any>) = this.shouldHaveTag(pair.first, pair.second)
fun View.shouldHaveTag(key: Int, value: Any) = this should haveTag(key, value)
infix fun View.shouldNotHaveTag(pair: Pair<Int, Any>) = this.shouldNotHaveTag(pair.first, pair.second)
fun View.shouldNotHaveTag(key: Int, value: Any) = this shouldNot haveTag(key, value)

fun haveTag(key: Int, tagValue: Any) = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.getTag(key) == tagValue,
        "View should have tag with Key $key and value $tagValue, but value was $tagValue",
        "View should not have tag with Key $key and value $tagValue, but had"
    )
}

infix fun View.shouldHaveTag(value: Any) = this should haveTag(value)
infix fun View.shouldNotHaveTag(value: Any) = this shouldNot haveTag(value)

fun haveTag(tagValue: Any) = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.tag == tagValue,
        "View should have tag with value $tagValue, but was ${value.tag}",
        "View should not have tag with value $tagValue, but had"
    )
}

fun View.shouldBeEnabled() = this should beEnabled()
fun View.shouldNotBeEnabled() = this shouldNot beEnabled()

fun beEnabled() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.isEnabled,
        "View should be enabled, but isn't",
        "View should not be enabled, but is"
    )
}

fun View.shouldBeFocused() = this should beFocused()
fun View.shouldNotBeFocused() = this shouldNot beFocused()

fun beFocused() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.isFocused,
        "View should be focused, but isn't",
        "View should not be focused, but is"
    )
}

fun View.shouldBeFocusable() = this should beFocusable()
fun View.shouldNotBeFocusable() = this shouldNot beFocusable()

fun beFocusable() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.isFocusable,
        "View should be focusable, but isn't",
        "View should not be focusable, but is"
    )
}

fun View.shouldBeFocusableInTouchMode() = this should beFocusableInTouchMode()
fun View.shouldNotBeFocusableInTouchMode() = this shouldNot beFocusableInTouchMode()

fun beFocusableInTouchMode() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.isFocusableInTouchMode,
        "View should be focusable in touch mode, but isn't",
        "View should not be focusable in touch mode, but is"
    )
}

fun View.shouldBeClickable() = this should beClickable()
fun View.shouldNotBeClickable() = this shouldNot beClickable()

fun beClickable() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.isClickable,
        "View should be clickable, but isn't",
        "View should not be clickable, but is"
    )
}

fun View.shouldBeLongClickable() = this should beLongClickable()
fun View.shouldNotBeLongClickable() = this shouldNot beLongClickable()

fun beLongClickable() = object : Matcher<View> {
    override fun test(value: View) = MatcherResult(
        value.isLongClickable,
        "View should be long clickable, but isn't",
        "View should not be long clickable, but is"
    )
}
