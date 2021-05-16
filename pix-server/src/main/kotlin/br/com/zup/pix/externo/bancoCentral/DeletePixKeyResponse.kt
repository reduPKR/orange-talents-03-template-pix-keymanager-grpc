package br.com.zup.pix.externo.bancoCentral

import java.time.LocalDateTime

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
) {

}
