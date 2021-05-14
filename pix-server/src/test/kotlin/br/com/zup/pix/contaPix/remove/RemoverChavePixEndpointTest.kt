package br.com.zup.pix.contaPix.remove

import br.com.zup.pix.*
import br.com.zup.pix.contaPix.ChavePix
import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.itau.*
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoverChavePixEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: PixServerRemoveServiceGrpc.PixServerRemoveServiceBlockingStub
) {
    @Inject
    lateinit var itauClient: ContaClienteItau
    lateinit var chavePixId: String
    val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        val chave = ChavePix(
            UUID.fromString(clienteId),
            TipoChave.EMAIL,
            "rafale@email.com",
            TipoConta.CONTA_CORRENTE,
            ContaAssociada(
                "ITAÚ UNIBANCO S.A.",
                "Rafael M C Ponte",
                "02467781054",
                "0001",
                "291900"
            )
        )
        val chavePix = repository.save(chave)
        chavePixId = chavePix.id.toString()
    }

    @Test
    fun `deve remover uma chave pix`() {
        assertEquals(1, repository.count())

        val dadosCliente = getDadosCliente()
        Mockito.`when`(itauClient.buscarCliente(clienteId))
            .thenReturn(HttpResponse.ok(dadosCliente))

        val chavePixRequest = RemoverChaveRequest.newBuilder()
            .setPixId(chavePixId)
            .setClienteId(clienteId)
            .build()

        val response = grpcClient.remover(chavePixRequest)

        assertEquals(chavePixId, response.pixId)
        assertEquals(clienteId, response.clienteId)
    }

    private fun getDadosCliente(): DadosClienteResponse {
        return DadosClienteResponse(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "Rafael M C Ponte",
            "02467781054",
            InstituicaoResponse(
                "ITAÚ UNIBANCO S.A.",
                "60701190"
            )
        )
    }

    @MockBean(ContaClienteItau::class)
    fun itauMock(): ContaClienteItau {
        return Mockito.mock(ContaClienteItau::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : PixServerRemoveServiceGrpc.PixServerRemoveServiceBlockingStub? {
            return PixServerRemoveServiceGrpc.newBlockingStub(channel)
        }
    }
}