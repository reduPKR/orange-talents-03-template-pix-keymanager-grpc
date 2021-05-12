package br.com.zup.pix.registra

import br.com.zup.pix.exception.ChavePixExistenteException
import br.com.zup.pix.exception.ClienteNaoEncontradoException
import br.com.zup.pix.itau.ContaClienteItau
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid

class ChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ContaClienteItau
) {

    @Transactional
    fun registrar(@Valid chavePixRequest: ChavePixRequest): ChavePix {

        if (repository.existsByChave(chavePixRequest.chave)){
            throw ChavePixExistenteException("Chave: ${chavePixRequest.chave} já foi cadastrada")
        }

        val response = itauClient.buscarContaPorTipo(chavePixRequest.clienteId!!, chavePixRequest.tipoConta!!.name)
        val conta = response.body()?.toModel()
            ?: throw ClienteNaoEncontradoException("Cliente não encontrado")

        val chave = chavePixRequest.toModel(conta)
        repository.save(chave)
        return chave
    }

}
