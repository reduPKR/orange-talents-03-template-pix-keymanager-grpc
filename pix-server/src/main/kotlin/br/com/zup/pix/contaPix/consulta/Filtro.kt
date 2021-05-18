package br.com.zup.pix.contaPix.consulta

import br.com.zup.pix.validador.ValidUUID
import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.exception.ChavePixNaoExisteException
import br.com.zup.pix.exception.ChavePixNaoPertenceAoCliente
import br.com.zup.pix.exception.ClienteNaoEncontradoException
import br.com.zup.pix.externo.bancoCentral.BancoCentralCliente
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.lang.IllegalArgumentException
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {
    abstract fun filtrar(repository: ChavePixRepository, bcCliente: BancoCentralCliente) : ChavePixInfo

    @Introspected
    data class porPixId(
        @field:NotBlank @ValidUUID val clienteId: String,
        @field:NotBlank @ValidUUID val pixId: String
    ): Filtro() {
        val pixUUID = UUID.fromString(pixId)
        val clienteUUID = UUID.fromString(clienteId)

        override fun filtrar(repository: ChavePixRepository, bcCliente: BancoCentralCliente): ChavePixInfo {
            return repository
                .findByIdAndClienteId(pixUUID, clienteUUID)
                .map(ChavePixInfo::converter)
                .orElseThrow{
                    when {
                        !repository.existsById(pixUUID) -> {
                            ChavePixNaoExisteException("Pix id não encontrada")
                        }
                        !repository.existsByClienteId(clienteUUID) -> {
                            ClienteNaoEncontradoException("Cliente não encontrado")
                        }
                        else -> {
                            ChavePixNaoPertenceAoCliente("Chave não pertence ao cliente")
                        }
                    }
                }
        }
    }

    @Introspected
    data class porChave(@field:NotBlank @Size(max = 77) val chave: String): Filtro() {
        override fun filtrar(repository: ChavePixRepository, bcCliente: BancoCentralCliente): ChavePixInfo {
            return repository
                .findByChave(chave)
                .map(ChavePixInfo::converter)
                .orElseGet{
                    val response = bcCliente.consultarChave(chave)
                    when(response.status){
                        HttpStatus.OK -> response.body()?.toModel()
                        else -> throw ChavePixNaoExisteException("Chave pix não encontrada")
                    }
                }
        }
    }

    @Introspected
    class invalido() : Filtro() {
        override fun filtrar(repository: ChavePixRepository, bcCliente: BancoCentralCliente): ChavePixInfo {
            throw IllegalArgumentException("Chave pix não encontrada")
        }
    }
}
