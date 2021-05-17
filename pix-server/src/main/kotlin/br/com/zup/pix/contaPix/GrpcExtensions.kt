package br.com.zup.pix.contaPix.registra

import br.com.zup.pix.ConsultarChavePixRequest
import br.com.zup.pix.RegistrarChaveRequest
import br.com.zup.pix.RemoverChaveRequest
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.contaPix.remove.RemoverChavePixRequest
import io.micronaut.validation.validator.Validator
import javax.validation.ConstraintViolationException

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

fun ConsultarChavePixRequest.toModel(validator: Validator): Filtro {
    val filtro = when(filtroCase){
        PIXID -> let {
            Filtro.PorPixId(it.clienteId, it.pixId)
        }
        CHAVE -> Filtro.PorChave(chave)
        FILTRO_NOT_SET -> Filtro.invalido()
    }

    val validator = validator.validate(filtro)
    if(validator.isNotEmpty()){
        throw ConstraintViolationException(validator)
    }

    return filtro
}