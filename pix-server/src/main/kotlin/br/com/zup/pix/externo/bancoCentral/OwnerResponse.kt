package br.com.zup.pix.externo.bancoCentral

data class OwnerResponse(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
) {

}
