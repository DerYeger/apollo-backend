package eu.yeger.apollo.user

import eu.yeger.apollo.user.model.api.CreateUserRequest
import eu.yeger.apollo.user.model.api.UpdateUserRequest
import eu.yeger.apollo.user.model.api.toUser
import eu.yeger.apollo.user.service.UserService
import eu.yeger.apollo.utils.delete
import eu.yeger.apollo.utils.get
import eu.yeger.apollo.utils.getParameter
import eu.yeger.apollo.utils.post
import eu.yeger.apollo.utils.put
import io.ktor.auth.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

public fun Route.userRoutes() {
  val userService: UserService by inject()

  authenticate {
    route("users") {
      get {
        userService.getAll()
      }

      post { user: CreateUserRequest ->
        userService.create(user.toUser())
      }

      put { user: UpdateUserRequest -> userService.update(user.toUser()) }

      route("{id}") {
        get {
          userService.getById(getParameter("id"))
        }

        delete {
          userService.deleteById(getParameter("id"))
        }
      }
    }
  }
}
