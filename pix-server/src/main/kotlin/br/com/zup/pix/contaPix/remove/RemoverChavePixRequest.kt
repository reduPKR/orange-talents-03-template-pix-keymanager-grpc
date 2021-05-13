package br.com.zup.pix.contaPix.remove

import br.com.zup.pix.compartilhado.ValideUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
class RemoverChavePixRequest(
    @ValideUUID
    @field:NotBlank
    val clienteId: String?,
    @ValideUUID
    @field:NotBlank
    val pixId: String?,
) {

}
