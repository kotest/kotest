package classes

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.plugin.intellij.AbstractSpec

class Child1 : AbstractSpec()
class Child2 : FunSpec()
class Child3 : StringSpec()
class Child4 : BehaviorSpec()
class Child5 : ExpectSpec()
class Child6 : FeatureSpec()
class Child7 : FreeSpec()
class Child8 : ShouldSpec()
class Child9 : WordSpec()
class Child10 : DescribeSpec()
class Child11 : java.util.LinkedList<String>()
class Child12
