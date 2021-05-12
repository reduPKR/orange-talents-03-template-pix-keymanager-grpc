package br.com.zup.pix.registra

import br.com.zup.pix.compartilhado.ValideUUID
import br.com.zup.pix.itau.ContaAssociada
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey //implementar depois
@Introspected
class ChavePixRequest(
    @ValideUUID
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
