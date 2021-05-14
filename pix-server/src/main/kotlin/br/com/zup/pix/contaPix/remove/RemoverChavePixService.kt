package br.com.zup.pix.contaPix.remove

import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.exception.ChavePixNaoExisteException
import br.com.zup.pix.exception.ChavePixNaoPertenceAoCliente
import br.com.zup.pix.exception.ClienteNaoEncontradoException
import br.com.zup.pix.exception.ErroAoRetornarChavePixException
import br.com.zup.pix.externo.itau.ContaClienteItau
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
    @Inject val itauClient: ContaClienteItau
) {

    @Transactional
    fun remover(@Valid chavePixRequest: RemoverChavePixRequest) {
        val pixId = UUID.fromString(chavePixRequest.pixId!!)
        val clienteId = UUID.fromString(chavePixRequest.clienteId!!)

        if(!repository.existsById(pixId)){
            throw ChavePixNaoExisteException("Chave Pix não localizada")
        }

        val response = itauClient.buscarCliente(chavePixRequest.clienteId)
        val cliente = response.body()
            ?: throw ClienteNaoEncontradoException("Cliente não encontrado")

        if(!repository.existsByIdAndClienteId(pixId,
               clienteId)){
            throw ChavePixNaoPertenceAoCliente("Chave Pix não pertence ao cliente informado")
        }

        val chavePix = repository.findByIdAndClienteId(pixId, clienteId)
        if(chavePix.isEmpty)
            throw ErroAoRetornarChavePixException("Erro ao tentar remover a chave")

        repository.delete(chavePix.get())
    }

}
