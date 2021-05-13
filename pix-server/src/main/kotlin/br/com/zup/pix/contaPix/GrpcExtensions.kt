package br.com.zup.pix.contaPix.registra

import br.com.zup.pix.RegistrarChaveRequest
import br.com.zup.pix.RemoverChaveRequest
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.contaPix.remove.RemoverChavePixRequest

fun RegistrarChaveRequest.toModel() : RegistrarChavePixRequest {
    return RegistrarChavePixRequest(
        clienteId = clienteId,
        tipoChave = TipoChave.valueOf(tipoChave.name),
        chave = chave,
        tipoConta = TipoConta.valueOf(tipoConta.name)
    )
}

fun RemoverChaveRequest.toModel(): RemoverChavePixRequest {
    return RemoverChavePixRequest(clienteId,pixId)
}
