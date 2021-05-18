package br.com.zup.pix.contaPix.consulta

import br.com.zup.pix.ConsultarChavePixRequest
import br.com.zup.pix.ConsultarChavePixResponse
import br.com.zup.pix.PixServerConsultarServiceGrpc
import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.contaPix.registra.toModel
import br.com.zup.pix.externo.bancoCentral.BancoCentralCliente
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
        val chaveResponse = filtro.filtrar(repository, bcCliente)

        responseObserver.onNext(ConsultarChavePixConverter().converter(chaveResponse))
        responseObserver.onCompleted()
    }
}