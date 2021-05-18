package br.com.zup.pix.contaPix.consulta

import br.com.zup.pix.contaPix.ChavePix
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.externo.itau.ContaAssociada
import java.time.LocalDateTime
import java.util.*

data class ChavePixInfo(
    val pixId: UUID? = null,
    val clientId: UUID? = null,
    val tipoChave: TipoChave,
    val chave: String,
    val tipoConta: TipoConta,
    val conta: ContaAssociada,
    val registradaEm: LocalDateTime = LocalDateTime.now()
) {
    companion object{
        fun converter(chave: ChavePix): ChavePixInfo {
            return ChavePixInfo(
                chave.id,
                chave.clienteId,
                chave.tipoChave,
                chave.chave,
                chave.tipoConta,
                chave.conta,
                chave.criadaEm
            )
        }
    }
}
