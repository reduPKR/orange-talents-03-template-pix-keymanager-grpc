package br.com.zup.pix.contaPix.consulta

import br.com.zup.pix.ConsultarChavePixRequest
import br.com.zup.pix.ConsultarChavePixResponse
import br.com.zup.pix.PixServerConsultarServiceGrpc
import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.contaPix.registra.toModel
import br.com.zup.pix.exception.ChavePixNaoExisteException
import br.com.zup.pix.exception.ChavePixNaoPertenceAoCliente
import br.com.zup.pix.exception.ClienteNaoEncontradoException
import br.com.zup.pix.externo.bancoCentral.BancoCentralCliente
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.validation.validator.Validator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsultaChaveEndpoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val bcCliente: BancoCentralCliente,
    @Inject private val validator: Validator
): PixServerConsultarServiceGrpc.PixServerConsultarServiceImplBase() {
    override fun consultar(
        request: ConsultarChavePixRequest,
        responseObserver: StreamObserver<ConsultarChavePixResponse>
    ) {
        val filtro = request.toModel(validator)

        try{
            val chaveResponse = filtro.filtrar(repository, bcCliente)
            responseObserver.onNext(ConsultarChavePixConverter().converter(chaveResponse))
            responseObserver.onCompleted()
        }catch (e: ChavePixNaoExisteException){
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Chave PIX não foi encontrada")
                    .asRuntimeException()
            )
        }catch (e: ClienteNaoEncontradoException){
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Cliente não foi encontrado")
                    .asRuntimeException()
            )
        }catch (e: ChavePixNaoPertenceAoCliente){
            responseObserver.onError(
                Status.PERMISSION_DENIED
                    .withDescription("Chave PIX não pertence ao cliente")
                    .asRuntimeException()
            )
        }
        catch (e: Exception){
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Chave PIX não foi encontrada")
                    .asRuntimeException()
            )
        }

    }
}