package eu.yeger.apollo.fol

import com.github.michaelbull.result.*
import eu.yeger.apollo.model.api.Feedback
import eu.yeger.apollo.model.domain.Edge
import eu.yeger.apollo.model.domain.Graph
import eu.yeger.apollo.model.domain.Node
import eu.yeger.apollo.model.domain.fol.*
import eu.yeger.apollo.model.dto.TranslationDTO

/**
 * [Result] that either contains the result of the ModelChecking algorithm or the translation key of an error message.
 *
 * @author Jan Müller
 */
public typealias ModelCheckerResult = Result<ModelCheckerTrace, TranslationDTO>

/**
 * Performs the ModelChecking algorithm for the given [Graph], [FormulaHead] and with the selected [Feedback]-option.
 *
 * @param graph The [Graph] that will be checked.
 * @param formulaHead The [Formula] that will be checked as well as its meta-information.
 * @param feedback The selected [Feedback].
 * @return The calculated [ModelCheckerResult].
 */
public fun checkModel(graph: Graph, formulaHead: FormulaHead, feedback: Feedback): ModelCheckerResult = binding {
  val symbolTable = graph.loadSymbols()
    .andThen { symbolTable -> formulaHead.loadSymbols(symbolTable) }
    .andThen { symbolTable -> checkTotality(graph, symbolTable) }.bind()
  runCatching {
    when (feedback) {
      Feedback.Full -> formulaHead.formula.fullCheck(graph, symbolTable, emptyMap(), true)
      Feedback.Relevant -> formulaHead.formula.partialCheck(graph, symbolTable, emptyMap(), true)
      Feedback.Minimal ->
        formulaHead.formula.partialCheck(graph, symbolTable, emptyMap(), true)
          .copy(children = null)
    }
  }.mapError { error ->
    when (error) {
      is OutOfMemoryError -> TranslationDTO("api.error.request-too-big")
      else -> TranslationDTO(error.message ?: error.printStackTrace().let { "api.error.unknown" })
    }
  }.bind()
}

/**
 * Creates a validated [ModelCheckerTrace] using the given information.
 *
 * @receiver The source [Formula].
 * @param description [TranslationDTO] with a description key for this check.
 * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
 * @param shouldBeModel Indicates that the [Graph] is supposed to be model of checked [Formula].
 * @param children [List] of child-traces.
 *
 * @author Jan Müller
 */
internal fun Formula.validated(
  description: TranslationDTO,
  variableAssignments: Map<String, Node>,
  shouldBeModel: Boolean,
  vararg children: ModelCheckerTrace,
) =
  ModelCheckerTrace(
    formula = this.toString(variableAssignments, false),
    description = description,
    isModel = true,
    shouldBeModel = shouldBeModel,
    children = children.toList().takeUnless { it.isEmpty() }
  )

/**
 * Creates a invalidated [ModelCheckerTrace] using the given information.
 *
 * @receiver The source [Formula].
 * @param description [TranslationDTO] with a description key for this check.
 * @param variableAssignments [Map] of [BoundVariable] names and [Node]s that will replace them in the [String] representation.
 * @param shouldBeModel Indicates that the [Graph] is supposed to be model of checked [Formula].
 * @param children [List] of child-traces.
 *
 * @author Jan Müller
 */
internal fun Formula.invalidated(
  description: TranslationDTO,
  variableAssignments: Map<String, Node>,
  shouldBeModel: Boolean,
  vararg children: ModelCheckerTrace,
) =
  ModelCheckerTrace(
    formula = this.toString(variableAssignments, false),
    description = description,
    isModel = false,
    shouldBeModel = shouldBeModel,
    children = children.toList().takeUnless { it.isEmpty() }
  )

/**
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Arno Ehle, Benedikt Hruschka
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Iterates over the graph and puts all found symbols in a symbol table.
 *
 * @return [Result] containing either the created [SymbolTable] or a [TranslationDTO] containing an error message.
 *
 * @author Arno Ehle
 * @author Benedikt Hruschka
 * @author Jan Müller
 */
private fun Graph.loadSymbols(): Result<SymbolTable, TranslationDTO> {
  val unarySymbols = mutableMapOf<String, MutableSet<Node>>()
  val binarySymbols = mutableMapOf<String, MutableSet<Edge>>()
  val symbolTypes = mutableMapOf<String, String>()
  nodes.forEach { node: Node ->
    node.constants.forEach { constant ->
      symbolTypes[constant] = "F-0"
      val relationSet = unarySymbols.getOrPut(constant) { mutableSetOf() }
      if (relationSet.isNotEmpty()) {
        return@loadSymbols Err(TranslationDTO(key = "api.error.duplicate-constant", "constant" to constant))
      }
      relationSet.add(node)
    }
    node.relations.forEach { relation ->
      symbolTypes[relation] = "P-1"
      val relationSet = unarySymbols.getOrPut(relation) { mutableSetOf() }
      relationSet.add(node)
    }
  }
  edges.forEach { edge: Edge ->
    edge.functions.forEach { function ->
      symbolTypes.putIfAbsent(function, "F-1")
      if (symbolTypes[function] != "F-1") {
        return@loadSymbols Err(TranslationDTO("api.error.different-arities", "symbol" to function))
      }
      val relationSet = binarySymbols.getOrPut(function) { mutableSetOf() }
      if (relationSet.any { otherEdge: Edge -> edge.source == otherEdge.source }) {
        return@loadSymbols Err(TranslationDTO("api.error.different-function-values", "function" to function))
      }
      relationSet.add(edge)
    }
    edge.relations.forEach { relation ->
      symbolTypes.putIfAbsent(relation, "P-2")
      if (symbolTypes[relation] != "P-2") {
        return@loadSymbols Err(TranslationDTO("api.error.different-arities", "symbol" to relation))
      }
      val relationSet = binarySymbols.getOrPut(relation) { mutableSetOf() }
      relationSet.add(edge)
    }
  }
  return Ok(SymbolTable(unarySymbols = unarySymbols, binarySymbols = binarySymbols, symbolTypes = symbolTypes))
}

/**
 * Iterates over a [FormulaHead] and adds new symbols to the [symbolTable].
 *
 * @receiver The source [FormulaHead].
 * @param symbolTable The [SymbolTable] that will be validated and expanded.
 * @return [Result] containing either the expanded [SymbolTable] or a [TranslationDTO] containing an error message.
 *
 * @author Arno Ehle
 * @author Benedikt Hruschka
 * @author Jan Müller
 */
private fun FormulaHead.loadSymbols(symbolTable: SymbolTable): Result<SymbolTable, TranslationDTO> {
  val unarySymbols = symbolTable.unarySymbols.toMutableMap()
  val binarySymbols = symbolTable.binarySymbols.toMutableMap()
  val symbolTypes = symbolTable.symbolTypes.toMutableMap()
  this.symbolTable.forEach { (symbol: String, type: String) ->
    symbolTypes.putIfAbsent(symbol, type)
    if (type == "P-1" || type == "F-0") {
      unarySymbols.putIfAbsent(symbol, HashSet())
    } else if (type == "P-2" || type == "F-1") {
      binarySymbols.putIfAbsent(symbol, HashSet())
    }
    val typeInGraph = symbolTypes[symbol]
    if (type != typeInGraph) { // types are different?
      if (type == "V") {
        return@loadSymbols Err(TranslationDTO("api.error.bound-variable-reuse", "symbol" to symbol))
      } else {
        return@loadSymbols Err(TranslationDTO("api.error.different-arities-formula", "symbol" to symbol))
      }
    }
  }
  return Ok(SymbolTable(unarySymbols = unarySymbols, binarySymbols = binarySymbols, symbolTypes = symbolTypes))
}

/**
 * Functions mus be left total. Therefore, this method checks if all function symbols are defined for all inputs.
 *
 * @param graph The [Graph] that will be checked.
 * @param symbolTable The [SymbolTable] that will be validated.
 * @return [Result] containing either the validated [SymbolTable] or a [TranslationDTO] containing an error message.
 *
 * @author Arno Ehle
 * @author Benedikt Hruschka
 * @author Jan Müller
 */
private fun checkTotality(graph: Graph, symbolTable: SymbolTable): Result<SymbolTable, TranslationDTO> {
  symbolTable.symbolTypes.forEach { (symbol: String, type: String) ->
    when (type) {
      "F-0" -> if (symbolTable.unarySymbols[symbol]!!.size != 1) {
        return@checkTotality Err(TranslationDTO("api.error.undefined-constant", "constant" to symbol))
      }
      "F-1" -> {
        val relationSet: Set<Edge> = symbolTable.binarySymbols[symbol]!!
        graph.nodes.forEach { node: Node ->
          if (relationSet.none { edge: Edge -> edge.source == node }) {
            return@checkTotality Err(TranslationDTO("api.error.function-totality", "function" to symbol))
          }
        }
      }
    }
  }
  return Ok(symbolTable)
}
