package br.com.zup.pix.externo.bancoCentral

import java.time.LocalDateTime

data class CreatePixKeyResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: LocalDateTime
) {

}
