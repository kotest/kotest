package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EscapeStringFilterTest : DescribeSpec({ // 🟢


    describe("include escape string \\n"){ // ⚪
        context("something..."){ // ⚪
            it("something..."){ // ⚪
                true shouldBe true
            }
        }
    }

    describe("something..."){ // 🟢
        context("include escape string \\n"){ // ⚪
            it("something true"){ // ⚪
                true shouldBe true
            }
        }

        context("something ..."){  // 🟢
            it("include escape string \\n"){  // 🟢
                true shouldBe true
            }
        }
    }
})
