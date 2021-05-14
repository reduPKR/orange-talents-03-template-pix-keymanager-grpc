package br.com.zup.pix.externo.bancoCentral

import br.com.zup.pix.contaPix.ChavePix
import br.com.zup.pix.externo.bancoCentral.CreatePixKeyRequest.Companion.converter
import br.com.zup.pix.externo.itau.ContaAssociada

class CreatePixKeyRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountRequest,
    val owner: OwnerRequest
) {
    companion object{
        fun converter(chave: ChavePix): CreatePixKeyRequest{
            return CreatePixKeyRequest(
                KeyType.converter(chave.tipoChave),
                chave.chave,
                BankAccountRequest(
                    ContaAssociada.ITAU_UNIBANCO_ISPB,
                    chave.conta.agencia,
                    chave.conta.numeroConta,
                    BankAccountRequest.AccountType.converter(chave.tipoConta)
                ),
                OwnerRequest()
            )
        }
    }
}
