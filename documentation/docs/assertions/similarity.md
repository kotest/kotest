---
id: similarity
title: Partial Matches
slug: similarity.html
---

If kotest fails to match a `String` or an instance of a data class, it may try to find something similar. 
For instance, in the following example two fields out of three match, so kotest considers `sweetGreenApple` to be 66.6% similar to sweetRedApple:

```kotlin
listOf(sweetGreenApple, sweetGreenPear) shouldContain (sweetRedApple)

(snip)

PossibleMatches:
 expected: Fruit(name=apple, color=red, taste=sweet),
  but was: Fruit(name=apple, color=green, taste=sweet),
  The following fields did not match:
    "color" expected: <"red">, but was: <"green">
```

By default, kotest will only consider pairs of objects that have more than 50% matching fields. If needed, we can change `similarityThresholdInPercent` in configuration.

Likewise, if kotest does not detect an exact match, it may try to find a similar `String`. In the output, the matching part of String is indicated with plus signs:

```kotlin
listOf("sweet green apple", "sweet red plum") shouldContain ("sweet green pear")

(snip)

PossibleMatches:
Match[0]: part of slice with indexes [0..11] matched actual[0..11]
Line[0] ="sweet green apple"
Match[0]= ++++++++++++-----
```

By default, searching for similar strings is only enabled for strings with lengthes between 8 and 1024. 
If needed, we can change configuration values named `minValueSubmatchingSize` and `maxSubtringSubmatchingSize`.
<br/>
<br/>
To disable searching for similar strings altogether, set `enabledSubmatchesInStrings` to `false` in configuration.
