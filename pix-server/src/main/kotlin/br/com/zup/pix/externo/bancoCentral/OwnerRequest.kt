package br.com.zup.pix.externo.bancoCentral

data class OwnerRequest (
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
    )
{}
