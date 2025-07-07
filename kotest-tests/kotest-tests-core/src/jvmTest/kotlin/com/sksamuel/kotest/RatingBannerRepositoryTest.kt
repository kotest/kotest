package com.sksamuel.kotest

import io.kotest.core.spec.style.BehaviorSpec

class RatingBannerRepositoryTest : BehaviorSpec({

   Given("Rating banner repository") {
      When("observing rating banner state with data in store") {
         Then("should return correct banner model") {
         }
      }

      When("observing rating banner state with null boolean value") {
         Then("should return model with defaults") {
         }
      }

      When("setting rating banner to show") {
         Then("should save appropriate values to data store") {
         }
      }

      When("hiding rating banner") {
         Then("should save appropriate values to data store") {
         }
      }

      When("acknowledging rating banner") {
         Then("should call rating banner service") {
         }
      }

      When("triggering in-app rating dialog") {
         Then("should emit true to in-app rating dialog state") {
         }
      }

      When("getting rating trigger") {
         Then("should return cached trigger value") {
         }
      }

      When("observing in-app rating dialog state") {
         Then("should have initial value of false") {
         }
      }
   }
})
