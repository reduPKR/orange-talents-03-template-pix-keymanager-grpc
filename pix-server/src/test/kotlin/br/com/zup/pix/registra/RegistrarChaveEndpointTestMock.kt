package br.com.zup.pix.registra

import br.com.zup.pix.PixServerServiceGrpc
import br.com.zup.pix.RegistrarChaveRequest
import br.com.zup.pix.TipoChave
import br.com.zup.pix.TipoConta
import br.com.zup.pix.itau.ContaClienteItau
import br.com.zup.pix.itau.DadosContaResponse
import br.com.zup.pix.itau.InstituicaoResponse
import br.com.zup.pix.itau.TitularResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistrarChaveEndpointTestMock(
    val repository: ChavePixRepository,
    val grpcClient: PixServerServiceGrpc.PixServerServiceBlockingStub
){
    @Inject
    lateinit var itauClient: ContaClienteItau

    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma nova chave`(){
        val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        val dadosConta = getDadosConta()
        Mockito.`when`(itauClient.buscarContaPorTipo(clienteId, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosConta))

        val chaveRequest = RegistrarChaveRequest.newBuilder()
            .setClienteId(clienteId)
            .setTipoChave(TipoChave.valueOf("CPF"))
            .setChave("02467781054")
            .setTipoConta(TipoConta.valueOf("CONTA_CORRENTE"))
            .build()

        val chaveResponse = grpcClient.registrar(chaveRequest)

        with(chaveResponse){
            assertNotNull(pixId)
            assertEquals("c56dfef4-7901-44fb-84e2-a2cefb157890", clienteId)
        }
    }

    private fun getDadosConta(): DadosContaResponse {
        val intituica = InstituicaoResponse(
            "ITAÚ UNIBANCO S.A.",
            "60701190"
        )

        val titularResponse = TitularResponse(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "Rafael M C Ponte",
            "02467781054"
        )

        return DadosContaResponse(
            "CONTA_CORRENTE",
            intituica,
            "0001",
            "291900",
            titularResponse
        )
    }

    @Test
    fun `nao deve cadastrar caso a chave ja foi cadastrada`(){
        val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"
        val chave = "02467781054"

        val dadosConta = getDadosConta()
        Mockito.`when`(itauClient.buscarContaPorTipo(clienteId, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosConta))

        val chaveRequest = RegistrarChaveRequest.newBuilder()
            .setClienteId(clienteId)
            .setTipoChave(TipoChave.valueOf("CPF"))
            .setChave(chave)
            .setTipoConta(TipoConta.valueOf("CONTA_CORRENTE"))
            .build()

        grpcClient.registrar(chaveRequest)
        val error = assertThrows<StatusRuntimeException>{
            grpcClient.registrar(chaveRequest)
        }

        with(error){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave: $chave já foi cadastrada", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar caso o cliente nao seja encontrado no sistema do itau`(){
        val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"

        Mockito.`when`(itauClient.buscarContaPorTipo(clienteId, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val chaveRequest = RegistrarChaveRequest.newBuilder()
            .setClienteId(clienteId)
            .setTipoChave(TipoChave.valueOf("CPF"))
            .setChave("02467781054")
            .setTipoConta(TipoConta.valueOf("CONTA_CORRENTE"))
            .build()

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.registrar(chaveRequest)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente não encontrado no sistema do Itau", status.description)
        }
    }

    @MockBean(ContaClienteItau::class)
    fun itauMock(): ContaClienteItau{
        return Mockito.mock(ContaClienteItau::class.java)
    }

    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixServerServiceGrpc.PixServerServiceBlockingStub?{
            return PixServerServiceGrpc.newBlockingStub(channel)
        }
    }
}