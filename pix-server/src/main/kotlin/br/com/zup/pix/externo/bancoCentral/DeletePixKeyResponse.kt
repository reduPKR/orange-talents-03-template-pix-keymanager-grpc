package br.com.zup.pix.externo.bancoCentral

import java.time.LocalDateTime

class DeletePixKeyResponse {
    var key: String = ""
    var participant: String =  ""
    var deletedAt: LocalDateTime = LocalDateTime.now()

    constructor()

    constructor(key: String, participant: String, deletedAt: LocalDateTime) {
        this.key = key
        this.participant = participant
        this.deletedAt = deletedAt
    }


}
