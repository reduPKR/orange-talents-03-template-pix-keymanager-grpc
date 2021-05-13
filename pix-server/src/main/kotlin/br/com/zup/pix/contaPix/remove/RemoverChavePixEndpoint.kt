package br.com.zup.pix.contaPix.remove

import br.com.zup.pix.PixServerRemoveServiceGrpc
import br.com.zup.pix.RemoverChaveRequest
import br.com.zup.pix.RemoverChaveResponse
import br.com.zup.pix.contaPix.registra.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoverChavePixEndpoint(@Inject private val service: RemoverChavePixService)
    :PixServerRemoveServiceGrpc.PixServerRemoveServiceImplBase(){

    override fun remover(request: RemoverChaveRequest,
                         responseObserver: StreamObserver<RemoverChaveResponse>) {

        val chavePixRequest = request.toModel()

        try {
            service.remover(chavePixRequest)
        }catch (e: Exception){
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Dados invalidos")
                    .asRuntimeException()
            )
            return
        }
    }
}