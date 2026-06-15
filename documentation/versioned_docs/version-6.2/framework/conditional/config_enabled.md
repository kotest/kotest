---
id: enabled_config_flags
title: Conditional tests with enabled flags
slug: enabled-config-flag.html
sidebar_label: Enabled Flags
---

Kotest supports disabling tests by setting a configuration flag on a test.
These configuration flags are very similar: `enabled`, `enabledIf`, and `enabledOrReasonIf`.

### Enabled

You can disable a test case simply by setting the config parameter `enabled` to `false`.
If you're looking for something like JUnit's `@Ignore`, this is for you.

```kotlin
"should do something".config(enabled = false) {
  // test here
}
```

You can use the same mechanism to run tests only under certain conditions.
For example you could run certain tests only on Linux systems using
[`SystemUtils.IS_OS_LINUX`](https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/SystemUtils.html#IS_OS_LINUX) from [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/).

```kotlin
"should do something".config(enabled = IS_OS_LINUX) {
  // test here
}
```

### Enabled if

If you want to use a function that is evaluated each time the test is invoked, then you can use `enabledIf`.
This function has the signature `(TestCase) -> Boolean`, so as you can see, you have access to the test at runtime
when evaluating if a test should be enabled or disabled.

For example, if we wanted to disable all tests that begin with the word "danger", but only when executing on Fridays,
then we could do this:

```kotlin
val disableDangerOnFridays: EnabledIf = { !(it.name.testName.startsWith("danger") && isFriday()) }

"danger Will Robinson".config(enabledIf = disableDangerOnFridays) {
  // test here
}

"safe Will Robinson".config(enabledIf = disableDangerOnFridays) {
 // test here
}
```

### Enabled or Reason If

There is a third variant of the enabled flag, called `enabledOrReasonIf` which allows you to return a reason for the test being disabled.
This variant has the signature `(TestCase) -> Enabled`, where
`Enabled` is a type that can contain a skip reason. This reason string is passed through to the test reports.

For example, we can re-write the earlier 'danger' example like this:

```kotlin
val disableDangerOnFridays: (TestCase) -> Enabled = {
   if (it.name.testName.startsWith("danger") && isFriday())
      Enabled.disabled("It's a friday, and we don't like danger!")
   else
      Enabled.enabled
}

"danger Will Robinson".config(enabledOrReasonIf = disableDangerOnFridays) {
  // test here
}

"safe Will Robinson".config(enabledOrReasonIf = disableDangerOnFridays) {
 // test here
}
```
