`kotest-assertions-shared` is a "hidden" module that is imported by both kotest framework and kotest assertions - things that you might want to use regardless of which parts of kotest you want to use.

For example, `shouldBe` is needed by both. But we don't just want to include all the assertions in with framework
because some people don't like kotest assertions, and some people don't want kotest framework.
