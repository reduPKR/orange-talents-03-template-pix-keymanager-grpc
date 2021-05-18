package br.com.zup.pix.contaPix.remove

import br.com.zup.pix.validador.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
class RemoverChavePixRequest(
    @ValidUUID
    @field:NotBlank
    val clienteId: String?,
    @ValidUUID
    @field:NotBlank
    val pixId: String?,
) {

}
