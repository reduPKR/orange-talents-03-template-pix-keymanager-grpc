package br.com.zup.pix.contaPix.remove

import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.exception.*
import br.com.zup.pix.externo.bancoCentral.BancoCentralCliente
import br.com.zup.pix.externo.bancoCentral.DeletePixKeyRequest
import br.com.zup.pix.externo.itau.ContaClienteItau
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class RemoverChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ContaClienteItau,
    @Inject val bcCliente: BancoCentralCliente
) {

    @Transactional
    fun remover(@Valid chavePixRequest: RemoverChavePixRequest) {
        val pixId = UUID.fromString(chavePixRequest.pixId!!)
        val clienteId = UUID.fromString(chavePixRequest.clienteId!!)

        if(!repository.existsById(pixId)){
            throw ChavePixNaoExisteException("Chave Pix não localizada")
        }

        val response = itauClient.buscarCliente(chavePixRequest.clienteId)

        if(response.body() == null)
            throw ClienteNaoEncontradoException("Cliente não encontrado")

        if(!repository.existsByIdAndClienteId(pixId,
               clienteId)){
            throw ChavePixNaoPertenceAoCliente("Chave Pix não pertence ao cliente informado")
        }

        val optional = repository.findByIdAndClienteId(pixId, clienteId)
        if(optional.isEmpty)
            throw ErroAoRetornarChavePixException("Erro ao tentar remover a chave")

        val chavePix = optional.get()
        repository.delete(chavePix)

        val request = DeletePixKeyRequest(chavePix.chave)
        val bcResponse = bcCliente.remover(chavePix.chave, request)

        if (bcResponse.status == HttpStatus.NOT_FOUND)
            throw ErroAoRemoverChavePixBancoCentralNotFound()

        if (bcResponse.status != HttpStatus.FORBIDDEN)
            throw ErroAoRemoverChavePixBancoCentralForbidden()

        if (bcResponse.status != HttpStatus.OK)
            throw ErroAoRemoverChavePixBancoCentral()
    }

}
