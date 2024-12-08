package io.kotest.assertions.yaml

import io.kotest.core.spec.style.StringSpec

class YamlMatchersTest: StringSpec() {
   init {
      "should be valid YAML" {
         """
            key: "value"
         """.shouldBeValidYaml()
      }
      "should not be valid YAML" {
         """
            items:
            - id: 001
            title: A Book
             author: Unknown
         """.shouldNotBeValidYaml()
      }
      "should equal YAML without quotes" {
         """
            key: value
         """ shouldEqualYaml "key: value"
      }
      "should equal YAML with single quotes" {
         """
            key: value
         """ shouldEqualYaml "key: value"
      }
      "should equal YAML with double quotes" {
         """
            key: "value"
         """ shouldEqualYaml "key: value"
      }
      "should not be equal YAML" {
         """
            key: "value"
         """ shouldNotEqualYaml "value: key"
      }
      "should equal YAML ignoring comments" {
         """
            # some comment
            key: "value"
         """ shouldEqualYaml "key: value"
      }
      "should not equal YAML when first is invalid" {
         """
            items:
            - id: 001
            title: A Book
             author: Unknown
         """ shouldNotEqualYaml "key: value"
      }
      "should not equal YAML when second is invalid" {
         "key: value" shouldNotEqualYaml """
            items:
            - id: 001
            title: A Book
             author: Unknown
         """
      }
   }
}
