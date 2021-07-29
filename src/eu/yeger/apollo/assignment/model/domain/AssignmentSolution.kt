package eu.yeger.apollo.assignment.model.domain

import eu.yeger.apollo.shared.model.domain.Graph

public data class AssignmentSolution(val assignmentId: String, val firstGraph: Graph, val secondGraph: Graph)
