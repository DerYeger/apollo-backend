package eu.yeger.apollo.assignment.service

import eu.yeger.apollo.assignment.model.api.ApiAssignment
import eu.yeger.apollo.assignment.model.api.ApiAssignmentSolution
import eu.yeger.apollo.assignment.model.api.AssignmentCheckResponse
import eu.yeger.apollo.assignment.model.domain.Assignment
import eu.yeger.apollo.shared.model.api.ApiResult

public interface AssignmentService {

  public suspend fun getAll(): ApiResult<List<ApiAssignment>>

  public suspend fun getById(id: String): ApiResult<ApiAssignment>

  public suspend fun create(assignment: Assignment): ApiResult<ApiAssignment>

  public suspend fun update(assignment: Assignment): ApiResult<ApiAssignment>

  public suspend fun deleteById(id: String): ApiResult<Unit>

  public suspend fun checkAssignment(apiSolution: ApiAssignmentSolution): ApiResult<AssignmentCheckResponse>
}
