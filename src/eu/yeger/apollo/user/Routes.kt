package eu.yeger.apollo.user

import eu.yeger.apollo.user.model.domain.User
import eu.yeger.apollo.user.service.UserService
import eu.yeger.apollo.utils.get
import eu.yeger.apollo.utils.getParameter
import eu.yeger.apollo.utils.post
import io.ktor.auth.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

public fun Route.userRoutes() {
  val userService: UserService by inject()

  route("users") {
    get {
      userService.getAll()
    }

    authenticate {
      post { user: User ->
        userService.create(user)
      }

      put { user: User -> userService.update(user) }
    }

    route("{id}") {
      get {
        userService.getById(getParameter("id"))
      }

      authenticate {
        delete {
          userService.deleteById(getParameter("id"))
        }
      }
    }
  }
}
