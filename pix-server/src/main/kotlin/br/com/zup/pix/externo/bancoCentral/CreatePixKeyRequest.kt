package br.com.zup.pix.externo.bancoCentral

class CreatePixKeyRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountRequest,
    val owner: OwnerRequest
) {

}
