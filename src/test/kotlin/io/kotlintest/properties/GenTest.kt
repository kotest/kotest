package io.kotlintest.properties

import io.kotlintest.matchers.be
import io.kotlintest.matchers.have
import io.kotlintest.specs.WordSpec
import java.util.*

/**
 *  @author Hannes Güdelhöfer
 */
class GenTest : WordSpec() {
    init {
        "Gen.string.nextPrintableString" should {
            "give out a argument long string" {
                val random = Random();
                var rand = random.nextInt(10000);
                if (rand <= 0)
                    rand = 0 - rand;

                Gen.string().nextPrintableString(rand).length shouldBe rand
            }.config(invocations = 100, threads = 8)
        }
        "Gen.choose<int, int>" should {
            "only give out numbers in the given range" {
                val random = Random();

                val max = random.nextInt(10000) + 10000;
                val min = random.nextInt(10000) - 10000;

                val rand = Gen.choose(min, max).generate();
                rand should be gte min
                rand should be lt max
            }.config(invocations = 10000, threads = 8)
        }
        "Gen.choose<long, long>" should {
            "only give out numbers in the given range" {
                val random = Random();

                val max = random.nextInt(10000) + 10000;
                val min = random.nextInt(10000) - 10000;

                val rand = Gen.choose(min.toLong(), max.toLong()).generate();
                rand should be gte min.toLong()
                rand should be lt max.toLong()

            }.config(invocations = 10000, threads = 8)
        }
        "Gen.forClassName" should {
            "gives the right result" {

                val table = table(headers("name"),
                        row("java.lang.String"),
                        row("kotlin.String"),
                        row("java.lang.Integer"),
                        row("kotlin.Int"),
                        row("java.lang.Long"),
                        row("kotlin.Long"),
                        row("java.lang.Boolean"),
                        row("kotlin.Boolean"),
                        row("java.lang.Float"),
                        row("kotlin.Float"),
                        row("java.lang.Double"),
                        row("kotlin.Double"))

                forAll(table) { clazz ->
                    val tmp = clazz.split(".");
                    Gen.forClassName(clazz).generate()!!.javaClass.name should have substring (tmp[tmp.size - 1])
                }
            }
            "throw an exception, with a wrong class" {
                shouldThrow<IllegalArgumentException> {
                    Gen.forClassName("This.is.not.a.valid.class")
                }
            }
        }
        "Gen.create" should {
            "create a Generaor with the given function" {
                Gen.create { 5 }.generate() shouldBe 5;

                var i = 0;
                val gen = Gen.create { i++ }
                for (n in 0..1000) {
                    gen.generate() shouldBe n;
                }
            }
        }
        "Gen.default" should {
            "generate the defaults for list" {

                val gen = Gen.default<List<Int>>();
                forAll(gen) {
                    inst ->
                    forAll(inst) {
                        i ->
                        (i is Int) shouldBe true
                    }
                    true
                }

            }


            "generate the defaults for set" {

                val gen = Gen.default<Set<String>>();
                forAll(gen) {
                    inst ->
                    forAll(inst) {
                        i ->
                        (i is String) shouldBe true
                    }
                    true

                }

            }

            "use forClass for everything else" {

                val table = table(headers("name"),
                        row("java.lang.String"),
                        row("kotlin.String"),
                        row("java.lang.Integer"),
                        row("kotlin.Int"),
                        row("java.lang.Long"),
                        row("kotlin.Long"),
                        row("java.lang.Boolean"),
                        row("kotlin.Boolean"),
                        row("java.lang.Float"),
                        row("kotlin.Float"),
                        row("java.lang.Double"),
                        row("kotlin.Double"))

                forAll(table) { clazz ->
                    val tmp = clazz.split(".");
                    Gen.forClassName(clazz).generate()!!.javaClass.name should have substring (tmp[tmp.size - 1])
                }
            }
            "throw an exeption, with a wrong class" {
                shouldThrow<IllegalArgumentException> {
                    Gen.forClassName("This.is.not.a.valid.class")
                }
            }
        }
        "gen.oneOf" should {
            "select one of a given list" {

                forAll(Gen.list(Gen.int())) {
                    list ->

                    list.size >= 1 ||
                            (0..1000).all {
                                list.contains(Gen.oneOf(list).generate())
                            }
                }
            }
        }
    }
}