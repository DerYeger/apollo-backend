package eu.yeger.gramofo.fol.graph

import eu.yeger.gramofo.model.api.TranslationDTO

data class ModelCheckerTrace(
    val formula: String,
    val description: TranslationDTO,
    val isModel: Boolean,
    val children: List<ModelCheckerTrace>
)
