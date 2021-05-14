package br.com.zup.pix.contaPix.registra

import br.com.zup.pix.contaPix.ChavePix
import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.exception.ChavePixExistenteException
import br.com.zup.pix.exception.ClienteNaoEncontradoException
import br.com.zup.pix.exception.ErroBancoCentralException
import br.com.zup.pix.externo.bancoCentral.BancoCentralCliente
import br.com.zup.pix.externo.bancoCentral.CreatePixKeyRequest
import br.com.zup.pix.externo.itau.ContaClienteItau
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class RegistrarChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ContaClienteItau,
    @Inject val bcClient: BancoCentralCliente
) {

    @Transactional
    fun registrar(@Valid registrarChavePixRequest: RegistrarChavePixRequest): ChavePix {

        if (repository.existsByChave(registrarChavePixRequest.chave)){
            throw ChavePixExistenteException("Chave: ${registrarChavePixRequest.chave} já foi cadastrada")
        }

        val response = itauClient.buscarContaPorTipo(registrarChavePixRequest.clienteId!!, registrarChavePixRequest.tipoConta!!.name)
        val conta = response.body()?.toModel()
            ?: throw ClienteNaoEncontradoException("Cliente não encontrado")

        val chave = registrarChavePixRequest.toModel(conta)
        repository.save(chave)

        /*Etapa nova*/
        val bcRequest = CreatePixKeyRequest.converter(chave)
        val bcResponse = bcClient.cadastrar(bcRequest)
        if(bcResponse.status != HttpStatus.CREATED)
            throw ErroBancoCentralException()

        chave.atualiza(bcResponse.body()!!.key)
        return chave
    }

}
