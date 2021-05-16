package br.com.zup.pix.contaPix.remove

import br.com.zup.pix.PixServerRemoveServiceGrpc
import br.com.zup.pix.RemoverChaveRequest
import br.com.zup.pix.RemoverChaveResponse
import br.com.zup.pix.contaPix.registra.toModel
import br.com.zup.pix.exception.*
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
            respostaSucesso(responseObserver, chavePixRequest)
        }catch (e: ChavePixNaoExisteException){
            catchPixNaoLocalizada(responseObserver)
        }catch (e: ClienteNaoEncontradoException){
            catchClienteNaoLocalizada(responseObserver)
        }catch (e: ChavePixNaoPertenceAoCliente){
            catchContaPixNaoPertenceAoCliente(responseObserver)
        }catch (e: ErroAoRetornarChavePixException){
            catchErroNoServidor(responseObserver)
        }catch (e: ErroAoRemoverChavePixBancoCentralNotFound){
            catchChavePixBancoCentralNotFound(responseObserver)
        }catch (e: ErroAoRemoverChavePixBancoCentralForbidden){
            catchChavePixBancoCentralFobirdden(responseObserver)
        } catch (e: Exception){
            catchArgumentoInvalido(responseObserver)
        }
    }

    private fun respostaSucesso(
        responseObserver: StreamObserver<RemoverChaveResponse>,
        chavePixRequest: RemoverChavePixRequest
    ) {
        responseObserver.onNext(
            RemoverChaveResponse.newBuilder()
                .setClienteId(chavePixRequest.clienteId)
                .setPixId(chavePixRequest.pixId)
                .build()
        )
        responseObserver.onCompleted()
    }

    private fun catchChavePixBancoCentralNotFound(responseObserver: StreamObserver<RemoverChaveResponse>) {
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("PIX não localizado no sistema")
                .asRuntimeException()
        )
    }

    private fun catchChavePixBancoCentralFobirdden(responseObserver: StreamObserver<RemoverChaveResponse>) {
        responseObserver.onError(
            Status.fromCodeValue(403)
                .withDescription("Servidor não autorizou a ação")
                .asRuntimeException()
        )
    }

    private fun catchErroNoServidor(responseObserver: StreamObserver<RemoverChaveResponse>) {
        responseObserver.onError(
            Status.fromCodeValue(500)
                .withDescription("Erro ao acessar o servidor")
                .asRuntimeException()
        )
    }

    private fun catchContaPixNaoPertenceAoCliente(responseObserver: StreamObserver<RemoverChaveResponse>) {
        responseObserver.onError(
            Status.PERMISSION_DENIED
                .withDescription("Conta PIX não pertence a este usuario")
                .asRuntimeException()
        )
    }

    private fun catchClienteNaoLocalizada(responseObserver: StreamObserver<RemoverChaveResponse>) {
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("Cliente não localizado no sistema do Itau")
                .asRuntimeException()
        )
    }

    private fun catchPixNaoLocalizada(responseObserver: StreamObserver<RemoverChaveResponse>) {
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("PIX não localizado no sistema")
                .asRuntimeException()
        )
    }

    private fun catchArgumentoInvalido(responseObserver: StreamObserver<RemoverChaveResponse>) {
        responseObserver.onError(
            Status.INVALID_ARGUMENT
                .withDescription("Dados invalidos")
                .asRuntimeException()
        )
    }
}