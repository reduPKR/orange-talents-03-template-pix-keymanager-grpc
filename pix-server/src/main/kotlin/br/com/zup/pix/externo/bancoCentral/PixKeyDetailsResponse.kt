package br.com.zup.pix.externo.bancoCentral

import br.com.zup.pix.compartilhado.ListaInstituicoes
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.contaPix.consulta.ChavePixInfo
import br.com.zup.pix.externo.itau.ContaAssociada
import java.time.LocalDateTime

data class PixKeyDetailsResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: LocalDateTime
) {
    fun toModel(): ChavePixInfo {
        return ChavePixInfo(
            tipoChave = keyType.tipoChave!!,
            chave = key,
            tipoConta = converterConta(),
            conta = converterContaAssociada()
        )
    }

    private fun converterContaAssociada(): ContaAssociada {
        return ContaAssociada(
            ListaInstituicoes.encontarISPB(bankAccount.participant),
            owner.name,
            owner.taxIdNumber,
            bankAccount.branch,
            bankAccount.accountNumber
        )
    }

    private fun converterConta(): TipoConta {
        return when(bankAccount.accountType){
            BankAccountResponse.AccountType.CACC -> TipoConta.CONTA_CORRENTE
            BankAccountResponse.AccountType.SVGS -> TipoConta.CONTA_POUPANCA
        }
    }

}
