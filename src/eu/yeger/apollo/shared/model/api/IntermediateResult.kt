package eu.yeger.apollo.shared.model.api

import com.github.michaelbull.result.Result

public typealias IntermediateResult<T> = Result<T, ResponseEntity<TranslationDTO>>
