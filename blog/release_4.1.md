Release 4.1
======


Better shrink information
-------------------

```
Property test failed for inputs
0) sSW$HK3 .Ifr?^\(=`c1X^3fS6R4wUuu)
1) %_GL\N>^.^yXH mD%qnp9DsCZ%[U,&`hCd/`H;k-duq:ZL

Caused by org.opentest4j.AssertionFailedError: expected:<true> but was:<false>

Attempting to shrink arg "sSW$HK3 .Ifr?^\(=`c1X^3fS6R4wUuu)"
Shrink #1: "sSW$HK3 .Ifr?^\(=" fail
Shrink #2: "sSW$HK3 ." fail
Shrink #3: "sSW$H" fail
Shrink #4: "sSW" fail
Shrink #5: "sS" fail
Shrink #6: "s" fail
Shrink #7: <empty string> fail
Shrink result (after 7 shrinks) => <empty string>
Caused by org.opentest4j.AssertionFailedError: expected:<true> but was:<false> at
com.sksamuel.kotest.property.shrinking.ShrinkingTest$1$invokeSuspend$$inlined$forAll$9.invokeSuspend(propertyTest2.kt:148)
com.sksamuel.kotest.property.shrinking.ShrinkingTest$1$invokeSuspend$$inlined$forAll$9.invoke(propertyTest2.kt)
io.kotest.property.internal.ShrinkfnsKt$shrinkfn$2$invokeSuspend$$inlined$with$lambda$1.invokeSuspend(shrinkfns.kt:36)
io.kotest.property.internal.ShrinkfnsKt$shrinkfn$2$invokeSuspend$$inlined$with$lambda$1.invoke(shrinkfns.kt)
io.kotest.property.internal.ShrinkKt.doStep(shrink.kt:76)
io.kotest.property.internal.ShrinkKt.doStep(shrink.kt:83)


Attempting to shrink arg "%_GL\N>^.^yXH mD%qnp9DsCZ%[U,&`hCd/`H;k-duq:ZL"
Shrink #1: "%_GL\N>^.^yXH mD%qnp9Ds" fail
Shrink #2: "%_GL\N>^.^yX" fail
Shrink #3: "%_GL\N" fail
Shrink #4: "%_G" fail
Shrink #5: "%_" fail
Shrink #6: "%" fail
Shrink #7: <empty string> fail
Shrink result (after 7 shrinks) => <empty string>
Caused by org.opentest4j.AssertionFailedError: expected:<true> but was:<false> at
com.sksamuel.kotest.property.shrinking.ShrinkingTest$1$invokeSuspend$$inlined$forAll$9.invokeSuspend(propertyTest2.kt:148)
com.sksamuel.kotest.property.shrinking.ShrinkingTest$1$invokeSuspend$$inlined$forAll$9.invoke(propertyTest2.kt)
io.kotest.property.internal.ShrinkfnsKt$shrinkfn$2$invokeSuspend$$inlined$with$lambda$2.invokeSuspend(shrinkfns.kt:37)
io.kotest.property.internal.ShrinkfnsKt$shrinkfn$2$invokeSuspend$$inlined$with$lambda$2.invoke(shrinkfns.kt)
io.kotest.property.internal.ShrinkKt.doStep(shrink.kt:76)
io.kotest.property.internal.ShrinkKt.doStep(shrink.kt:83)
```
