package eu.yeger.apollo.assignment.service

import eu.yeger.apollo.assignment.model.api.ApiAssignment
import eu.yeger.apollo.assignment.model.api.ApiAssignmentSolution
import eu.yeger.apollo.assignment.model.api.AssignmentCheckResponse
import eu.yeger.apollo.shared.model.api.ApiResult

public interface AssignmentService {

  public suspend fun getAll(): ApiResult<List<ApiAssignment>>

  public suspend fun getById(id: String): ApiResult<ApiAssignment>

  public suspend fun create(apiAssignment: ApiAssignment): ApiResult<ApiAssignment>

  public suspend fun checkAssignment(apiSolution: ApiAssignmentSolution): ApiResult<AssignmentCheckResponse>
}
