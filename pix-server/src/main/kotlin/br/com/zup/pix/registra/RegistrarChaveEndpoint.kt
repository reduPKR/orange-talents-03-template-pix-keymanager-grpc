package br.com.zup.pix.registra

import br.com.zup.pix.ChaveRegistradaResponse
import br.com.zup.pix.PixServerServiceGrpc
import br.com.zup.pix.RegistrarChaveRequest
import io.grpc.stub.StreamObserver
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
        service.registrar(chavePixRequest, responseObserver)


    }
}
