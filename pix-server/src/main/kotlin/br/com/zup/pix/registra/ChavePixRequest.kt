package br.com.zup.pix.registra

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
class ChavePixRequest(
    @field:NotBlank
    val clienteId: String?,
    @field:NotNull
    val tipoChave: TipoChave?,
    @field:Size(max=77)
    val chave: String?,
    @field:NotNull
    val tipoConta: TipoConta?
) {

}
