package br.com.zup.pix.contaPix.registra

import br.com.zup.pix.ChaveRegistradaResponse
import br.com.zup.pix.PixServerRegistrarServiceGrpc
import br.com.zup.pix.RegistrarChaveRequest
import br.com.zup.pix.contaPix.ChavePix
import br.com.zup.pix.exception.ChavePixExistenteException
import br.com.zup.pix.exception.ClienteNaoEncontradoException
import br.com.zup.pix.exception.ErroBancoCentralException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrarChaveEndpoint(@Inject private val service: RegistrarChavePixService)
    : PixServerRegistrarServiceGrpc.PixServerRegistrarServiceImplBase(){
    override fun registrar(
        request: RegistrarChaveRequest,
        responseObserver: StreamObserver<ChaveRegistradaResponse>
    ) {
        val chavePixRequest = request.toModel()
        try {
            val chave = service.registrar(chavePixRequest)
            respostaChaveRegistrada(responseObserver, chave)
        }catch (e: ChavePixExistenteException){
            catchChaveJaCadastrada(responseObserver, chavePixRequest)
        }catch (e: ClienteNaoEncontradoException){
            catchClienteNaoEncontrado(responseObserver)
        }catch(e: ErroBancoCentralException){
            catchErroComBancoCentral(responseObserver)
        }catch (e: Exception){
            catchArgumentosInvalidos(responseObserver)
        }
    }

    private fun catchErroComBancoCentral(responseObserver: StreamObserver<ChaveRegistradaResponse>) {
        responseObserver.onError(
            Status.INTERNAL
                .withDescription("Erro na comunicação com banco central")
                .asRuntimeException()
        )
    }

    private fun respostaChaveRegistrada(
        responseObserver: StreamObserver<ChaveRegistradaResponse>,
        chave: ChavePix
    ) {
        responseObserver.onNext(
            ChaveRegistradaResponse
                .newBuilder()
                .setPixId(chave.id!!.toString())
                .setClienteId(chave.clienteId.toString())
                .build()
        )
        responseObserver.onCompleted()
    }

    private fun catchArgumentosInvalidos(responseObserver: StreamObserver<ChaveRegistradaResponse>) {
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
        registrarChavePixRequest: RegistrarChavePixRequest
    ) {
        responseObserver.onError(
            Status.ALREADY_EXISTS
                .withDescription("Chave: ${registrarChavePixRequest.chave} já foi cadastrada")
                .asRuntimeException()
        )
    }
}
