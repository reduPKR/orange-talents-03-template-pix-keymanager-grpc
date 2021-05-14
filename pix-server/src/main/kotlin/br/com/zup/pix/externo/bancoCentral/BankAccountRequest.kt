package br.com.zup.pix.externo.bancoCentral

import br.com.zup.pix.contaPix.TipoConta


data class BankAccountRequest(
    val participant: String,
    val branch:	String,
    val accountNumber: String,
    val accountType: AccountType
) {
    enum class AccountType {
        CACC,
        SVGS;

        companion object{
            fun converter(tipoConta: TipoConta): AccountType{
                if(tipoConta == TipoConta.CONTA_CORRENTE){
                 return CACC
                }
                return SVGS
            }
        }
    }
}
