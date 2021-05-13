package br.com.zup.pix.contaPix.registra

import br.com.zup.pix.RegistrarChaveRequest
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta

fun RegistrarChaveRequest.toModel() : ChavePixRequest {
    return ChavePixRequest(
        clienteId = clienteId,
        tipoChave = TipoChave.valueOf(tipoChave.name),
        chave = chave,
        tipoConta = TipoConta.valueOf(tipoConta.name)
    )
}
