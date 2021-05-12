package br.com.zup.pix.registra

import br.com.zup.pix.RegistrarChaveRequest

fun RegistrarChaveRequest.toModel() : ChavePixRequest{
    return ChavePixRequest(
        clienteId = clienteId,
        tipoChave = TipoChave.valueOf(tipoChave.name),
        chave = chave,
        tipoConta = TipoConta.valueOf(tipoConta.name)
    )
}
