package io.kotest.plugin.intellij

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec

abstract class MyCustomFunSpec : FunSpec()

abstract class MyCustomStringSpec : StringSpec()

abstract class AbstractParent()

interface SomeInterface

class Spec1 : MyCustomFunSpec()
class Spec2 : MyCustomStringSpec()
class Spec3 : FunSpec()
class Spec4 : StringSpec()
class Spec5 : DescribeSpec()
class Spec6 : BehaviorSpec()
class Spec7 : WordSpec()
class Spec8 : FreeSpec()
class Spec9 : ExpectSpec()
class Spec10 : FeatureSpec()
class Spec11 : ShouldSpec()

@Ignored
class Spec12 : ShouldSpec()

class Spec13 : AbstractParent()
class Spec14 : SomeInterface
class Spec15

