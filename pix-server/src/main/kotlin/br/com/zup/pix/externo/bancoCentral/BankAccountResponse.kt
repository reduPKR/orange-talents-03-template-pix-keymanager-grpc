package br.com.zup.pix.externo.bancoCentral

import br.com.zup.pix.contaPix.TipoConta

data class BankAccountResponse(
    val participant: String,
    val branch:	String,
    val accountNumber: String,
    val accountType: AccountType
) {
    enum class AccountType {
        CACC,
        SVGS;

        companion object{
            fun converter(tipoConta: TipoConta): BankAccountRequest.AccountType {
                if(tipoConta == TipoConta.CONTA_CORRENTE){
                    return BankAccountRequest.AccountType.CACC
                }
                return BankAccountRequest.AccountType.SVGS
            }
        }
    }
}
