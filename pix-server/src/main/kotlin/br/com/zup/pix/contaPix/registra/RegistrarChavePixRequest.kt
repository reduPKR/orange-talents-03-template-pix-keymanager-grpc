package br.com.zup.pix.contaPix.registra

import br.com.zup.pix.validador.ValidPixKey
import br.com.zup.pix.validador.ValidUUID
import br.com.zup.pix.contaPix.ChavePix
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.externo.itau.ContaAssociada
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
class RegistrarChavePixRequest(
    @ValidUUID
    @field:NotBlank
    val clienteId: String?,
    @field:NotNull
    val tipoChave: TipoChave?,
    @field:Size(max=77)
    val chave: String?,
    @field:NotNull
    val tipoConta: TipoConta?
) {
    fun toModel(conta: ContaAssociada): ChavePix {
        return ChavePix(
            UUID.fromString(this.clienteId),
            TipoChave.valueOf(this.tipoChave!!.name),
            if(this.tipoChave == TipoChave.ALEATORIA) UUID.randomUUID().toString() else this.chave!!,
            TipoConta.valueOf(this.tipoConta!!.name),
            conta
        )
    }

}
