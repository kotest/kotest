---
id: android_matchers
title: Android Matchers
slug: android-matchers.html
sidebar_label: Android
---



This page lists all current Android matchers in Kotest. These are additional to the default [matchers](matchers.md) and are specific to Android.

To use them, it's required to add an extra dependency to your project:
```kotlin
implementation("io.kotest:kotest-assertions-android:VERSION")
```

| View | |
| -------- | ---- |
| `view.shouldBeVisible()` | Asserts that the view visibility is VISIBLE |
| `view.shouldBeInvisible()` | Asserts that the view visibility is INVISIBLE |
| `view.shouldBeGone()` | Asserts that the view visibility is GONE |
| `view.shouldHaveContentDescription()` | Asserts that the view has any content description |
| `view.shouldHaveContentDescription(desc)` | Asserts that the view has `desc` as Content Description |
| `view.shouldHaveTag(key, value)` | Asserts that the view has a tag `key` with value `value` |
| `view.shouldHaveTag(any)` | Asserts that the view's tag is `any` |
| `view.shouldBeEnabled()` | Asserts that the view is enabled |
| `view.shouldBeFocused()` | Asserts that the view has focus |
| `view.shouldBeFocusable()` | Asserts that the view is focusable |
| `view.shouldBeFocusableInTouchMode()` | Asserts that the view is focusable in touch mode |
| `view.shouldBeClickable()` | Asserts that the view is clickable |
| `view.shouldBeLongClickable()` | Asserts that the view is long clickable |

| TextView | |
| -------- | ---- |
| `tv.shouldHaveText(text)` | Asserts that the text view has text `text` |
| `tv.shouldHaveTextColorId(id)` | Asserts that the text color is the same from color resource `id` |
| `tv.shouldHaveTextColor(colorInt)` | Asserts that the text color is `colorInt` |
| `tv.shouldBeAllCaps()` | Asserts that the textview is marked with the `isAllCaps` flag |
| `tv.shouldHaveTextAlignment(alignment)` | Asserts that the text alignment is `alignment` |
