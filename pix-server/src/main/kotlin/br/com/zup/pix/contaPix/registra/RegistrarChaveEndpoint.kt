package br.com.zup.pix.contaPix.registra

import br.com.zup.pix.ChaveRegistradaResponse
import br.com.zup.pix.PixServerRegistrarServiceGrpc
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
    : PixServerRegistrarServiceGrpc.PixServerRegistrarServiceImplBase(){
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
            responseObserver.onCompleted()
        }catch (e: ChavePixExistenteException){
            catchChaveJaCadastrada(responseObserver, chavePixRequest)
            return
        }catch (e: ClienteNaoEncontradoException){
            catchClienteNaoEncontrado(responseObserver)
            return
        }catch (e: Exception){
            argumentosInvalidos(responseObserver)
            return
        }
    }

    private fun argumentosInvalidos(responseObserver: StreamObserver<ChaveRegistradaResponse>) {
        responseObserver.onError(
            Status.INVALID_ARGUMENT
                .withDescription("Dados invalidos")
                .asRuntimeException()
        )
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
