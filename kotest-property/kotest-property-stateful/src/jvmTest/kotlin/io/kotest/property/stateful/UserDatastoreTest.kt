package io.kotest.property.stateful

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerSpecExtension
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

class UserDatastoreTest : FunSpec() {

   val postgres = install(JdbcDatabaseContainerSpecExtension(MySQLContainer(DockerImageName.parse("mysql:8.0"))))

   init {
      test("stateful testing example") {
         val actions = listOf(
            insert,
            updatePassword("newpassword"),
            delete,
            truncate
         )
         StatefulTester.of(actions).start(UserDatastore(postgres))
      }
   }
}

val insert = object : Action<UserDatastore> {

   override fun apply(state: UserDatastore): UserDatastore {
      state.insert(User("user1", "password1", 30))
      return state
   }
}

val delete = object : Action<UserDatastore> {

   override fun apply(state: UserDatastore): UserDatastore {
      state.delete("user1")
      return state
   }
}

val truncate = object : Action<UserDatastore> {

   override fun apply(state: UserDatastore): UserDatastore {
      // state.truncate()
      return state
   }
}

fun updatePassword(newPassword: String) = object : Action<UserDatastore> {

   override fun apply(state: UserDatastore): UserDatastore {
      state.updatePassword("user1", newPassword, "department")
      return state
   }
}
