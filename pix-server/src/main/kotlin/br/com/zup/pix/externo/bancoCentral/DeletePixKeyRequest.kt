package br.com.zup.pix.externo.bancoCentral

import br.com.zup.pix.externo.itau.ContaAssociada

class DeletePixKeyRequest(
    val key: String,
) {
    val participant = ContaAssociada.ITAU_UNIBANCO_ISPB
}
