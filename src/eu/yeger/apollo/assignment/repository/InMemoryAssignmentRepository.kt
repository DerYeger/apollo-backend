package eu.yeger.apollo.assignment.repository

import eu.yeger.apollo.assignment.model.persistence.PersistentAssignment
import eu.yeger.apollo.shared.repository.InMemoryRepository
import eu.yeger.apollo.shared.repository.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

public class InMemoryAssignmentRepository(
  private val assignmentMap: ConcurrentMap<String, PersistentAssignment> = ConcurrentHashMap()
) : AssignmentRepository, Repository<PersistentAssignment> by InMemoryRepository(assignmentMap)
