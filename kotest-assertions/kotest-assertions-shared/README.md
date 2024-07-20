`kotest-assertions-shared` is a "hidden" module imported by both kotest-framework and kotest-assertions.
It contains things that you might want to use regardless of which parts of Kotest you want to use.

For example, `shouldBe` is needed by both. But we don't just want to include all the assertions in with framework
because some people don't like kotest-assertions, and some people don't want kotest-framework.
