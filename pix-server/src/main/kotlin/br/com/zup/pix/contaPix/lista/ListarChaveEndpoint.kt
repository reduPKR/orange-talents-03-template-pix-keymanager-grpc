package br.com.zup.pix.contaPix.lista

import br.com.zup.pix.*
import br.com.zup.pix.contaPix.ChavePixRepository
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListarChaveEndpoint(
    @Inject private val repository: ChavePixRepository
): PixServerListarServiceGrpc.PixServerListarServiceImplBase() {
    override fun listar(
        request: ListarChavePixRequest,
        responseObserver: StreamObserver<ListarChavePixResponse>
    ) {
        if(request.clienteId.isNullOrBlank())
            throw IllegalArgumentException("Chave não pode ser nula ou vazia")

        val clienteUUID = UUID.fromString(request.clienteId)
        val lista = repository.findAllByClienteId(clienteUUID).map {
            ListarChavePixResponse.ChavePix.newBuilder()
                .setPixId(it.id.toString())
                .setTipoChave(TipoChave.valueOf(it.tipoChave.name))
                .setChave(it.chave)
                .setTipoConta(TipoConta.valueOf(it.tipoConta.name))
                .setCriadoEm(it.criadaEm.let {
                    val criadoEm = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(criadoEm.epochSecond)
                        .setNanos(criadoEm.nano)
                        .build()
                })
                .build()
        }

        responseObserver.onNext(
            ListarChavePixResponse.newBuilder()
                .setClienteId(clienteUUID.toString())
                .addAllListaChaves(lista)
                .build()
        )
        responseObserver.onCompleted()
    }
}