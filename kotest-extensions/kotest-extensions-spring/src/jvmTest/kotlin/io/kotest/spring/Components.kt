package io.kotest.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class Components {

  @Bean
  open fun userService(repo: DefaultRepository) = UserService(repo)

  @Bean
  open fun userRepository() = DefaultRepository()
}
