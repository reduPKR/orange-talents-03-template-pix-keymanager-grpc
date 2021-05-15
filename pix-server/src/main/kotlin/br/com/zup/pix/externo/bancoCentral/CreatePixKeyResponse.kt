package br.com.zup.pix.externo.bancoCentral

import java.time.LocalDateTime

class CreatePixKeyResponse {
    var keyType: KeyType = KeyType.CPF
    var key: String = ""
    var bankAccount: BankAccountRequest? = null
    var owner: OwnerRequest? = null
    var createdAt: LocalDateTime = LocalDateTime.now()

    constructor(keyType: KeyType, key: String, bankAccount: BankAccountRequest?, owner: OwnerRequest?) {
        this.keyType = keyType
        this.key = key
        this.bankAccount = bankAccount
        this.owner = owner
    }

    constructor(
        keyType: KeyType,
        key: String,
        bankAccount: BankAccountRequest?,
        owner: OwnerRequest?,
        createdAt: LocalDateTime
    ) {
        this.keyType = keyType
        this.key = key
        this.bankAccount = bankAccount
        this.owner = owner
        this.createdAt = createdAt
    }


}
