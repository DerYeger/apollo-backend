package eu.yeger.apollo.shared.model.api

import com.github.michaelbull.result.Result
/**
 * [Result] that either contains a [ResponseEntity] with the given type or a [ResponseEntity] containing a [TranslationDTO] for error messages.
 *
 * @param T The type of successful [Result]s.
 *
 * @author Jan Müller
 */
public typealias ApiResult<T> = Result<ResponseEntity<T>, ResponseEntity<TranslationDTO>>
