package com.sksamuel

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec


internal class EmptyCallStatusCheckTest : ShouldSpec() {

   override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerLeaf

   init {
      context("when the call was always empty") {
         context("the check") {
            should("return empty after the timeout") {
            }
         }
      }
      context("when the call has participants") {
         context("the check") {
            should("never return empty") {
            }
         }
         context("and then goes empty") {
            context("the check") {
               should("return empty after the timeout") {
               }
            }
            context("and then has participants again") {
               context("the check") {
                  should("never return empty") {
                  }
               }
            }
         }
      }
      context("when a custom timeout is passed") {
         context("the check") {
            should("return empty after the timeout") {
            }
         }
      }
   }
}
