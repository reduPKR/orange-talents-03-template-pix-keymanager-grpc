package br.com.zup.pix.registra

import br.com.zup.pix.ChaveRegistradaResponse
import br.com.zup.pix.PixServerServiceGrpc
import br.com.zup.pix.RegistrarChaveRequest
import br.com.zup.pix.exception.ChavePixExistenteException
import br.com.zup.pix.exception.ClienteNaoEncontradoException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrarChaveEndpoint(@Inject private val service: ChavePixService)
    : PixServerServiceGrpc.PixServerServiceImplBase(){
    override fun registrar(
        request: RegistrarChaveRequest,
        responseObserver: StreamObserver<ChaveRegistradaResponse>
    ) {

        val chavePixRequest = request.toModel()
        try {
            val chave = service.registrar(chavePixRequest)

            responseObserver.onNext(ChaveRegistradaResponse
                .newBuilder()
                .setPixId(chave.id!!.toString())
                .setClienteId(chave.clienteId.toString())
                .build())

        }catch (e: ChavePixExistenteException){
            catchChaveJaCadastrada(responseObserver, chavePixRequest)
        }catch (e: ClienteNaoEncontradoException){
            catchClienteNaoEncontrado(responseObserver)
        }finally {
            responseObserver.onCompleted()
            return
        }
    }

    private fun catchClienteNaoEncontrado(responseObserver: StreamObserver<ChaveRegistradaResponse>) {
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("Cliente não encontrado no sistema do Itau")
                .asRuntimeException()
        )
    }

    private fun catchChaveJaCadastrada(
        responseObserver: StreamObserver<ChaveRegistradaResponse>,
        chavePixRequest: ChavePixRequest
    ) {
        responseObserver.onError(
            Status.ALREADY_EXISTS
                .withDescription("Chave: ${chavePixRequest.chave} já foi cadastrada")
                .asRuntimeException()
        )
    }
}
