package eu.yeger.apollo.user.service

import eu.yeger.apollo.shared.model.api.ApiResult
import eu.yeger.apollo.shared.model.api.ApiToken
import eu.yeger.apollo.user.model.api.ApiUser
import eu.yeger.apollo.user.model.api.Credentials
import eu.yeger.apollo.user.model.domain.User

public interface UserService {

  public suspend fun getAll(): ApiResult<List<ApiUser>>

  public suspend fun getById(id: String): ApiResult<ApiUser>

  public suspend fun create(user: User): ApiResult<ApiUser>

  public suspend fun update(user: User): ApiResult<ApiUser>

  public suspend fun deleteById(id: String): ApiResult<Unit>

  public suspend fun createDefaultUserIfRequired()

  public suspend fun login(credentials: Credentials): ApiResult<ApiToken>
}
