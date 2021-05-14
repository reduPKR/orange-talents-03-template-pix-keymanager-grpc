package br.com.zup.pix.externo.bancoCentral

import br.com.zup.pix.contaPix.TipoConta


class BankAccountRequest(
    participant: String,
    branch:	String,
    accountNumber: String,
    accountType: AccountType
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
