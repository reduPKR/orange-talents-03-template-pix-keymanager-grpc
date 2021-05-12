package br.com.zup.pix.registra

import br.com.zup.pix.ChaveRegistradaResponse
import br.com.zup.pix.itau.ContaClienteItau
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid

class ChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ContaClienteItau
) {

    @Transactional
    fun registrar(@Valid chavePixRequest: ChavePixRequest, responseObserver: StreamObserver<ChaveRegistradaResponse>): ChavePix {

        if (repository.existsByChave(chavePixRequest.chave)){
            responseObserver.onError(
                Status.ALREADY_EXISTS
                .withDescription("Chave: ${chavePixRequest.chave} já foi cadastrada")
                .asRuntimeException())

            responseObserver.onCompleted()
        }

        val response = itauClient.buscarContaPorTipo(chavePixRequest.clienteId!!, chavePixRequest.tipoConta!!.name)
        val conta = response.body()?.toModel()
            ?: {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription("Cliente não encontrado no sistema do Itau")
                        .asRuntimeException())

                responseObserver.onCompleted()
            }

        val chave = chavePixRequest.toModel(conta)
        repository.save(chave)
        return chave
    }

}
