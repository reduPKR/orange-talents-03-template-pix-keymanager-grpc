package br.com.zup.pix.contaPix.remove

import br.com.zup.pix.PixServerRemoveServiceGrpc
import br.com.zup.pix.RemoverChaveRequest
import br.com.zup.pix.contaPix.ChavePix
import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.externo.bancoCentral.BancoCentralCliente
import br.com.zup.pix.externo.bancoCentral.DeletePixKeyRequest
import br.com.zup.pix.externo.bancoCentral.DeletePixKeyResponse
import br.com.zup.pix.externo.itau.ContaAssociada
import br.com.zup.pix.externo.itau.ContaClienteItau
import br.com.zup.pix.externo.itau.DadosClienteResponse
import br.com.zup.pix.externo.itau.InstituicaoResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
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
    @Inject
    lateinit var bcCliente: BancoCentralCliente

    lateinit var chavePixId: String
    val clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890"
    val clienteId2 = "5260263c-a3c1-4727-ae32-3bdb2538841b"
    val chavePrincipal = "rafael@email.com"

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        val chave = ChavePix(
            UUID.fromString(clienteId),
            TipoChave.EMAIL,
            chavePrincipal,
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
        val dadosCliente = getDadosCliente()
        Mockito.`when`(itauClient.buscarCliente(clienteId))
            .thenReturn(HttpResponse.ok(dadosCliente))

//        Mockito.`when`(bcCliente.remover(chavePrincipal, DeletePixKeyRequest(chavePrincipal)))
//            .thenReturn(HttpResponse.ok())
            //doReturn(HttpResponse.ok()).when(bcClientMock).remover(...)
        doReturn(HttpResponse.ok(DeletePixKeyResponse()))
            .`when`(bcCliente).remover(chavePrincipal, DeletePixKeyRequest(chavePrincipal))

        val chavePixRequest = RemoverChaveRequest.newBuilder()
            .setPixId(chavePixId)
            .setClienteId(clienteId)
            .build()

        val response = grpcClient.remover(chavePixRequest)

        assertEquals(chavePixId, response.pixId)
        assertEquals(clienteId, response.clienteId)
    }

    @Test
    fun `nao deve remover uma chave pix se o banco central responder not found`() {
        val dadosCliente = getDadosCliente()
        Mockito.`when`(itauClient.buscarCliente(clienteId))
            .thenReturn(HttpResponse.ok(dadosCliente))

//        Mockito.`when`(bcCliente.remover(chavePrincipal, DeletePixKeyRequest(chavePrincipal)))
//            .thenReturn(HttpResponse.notFound())

        doReturn(HttpResponse.notFound(DeletePixKeyResponse()))
            .`when`(bcCliente).remover(chavePrincipal, DeletePixKeyRequest(chavePrincipal))

        val chavePixRequest = RemoverChaveRequest.newBuilder()
            .setPixId(chavePixId)
            .setClienteId(clienteId)
            .build()

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.remover(chavePixRequest)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("PIX não localizado no sistema", status.description)
        }
    }

    @Test
    fun `nao deve remover uma chave pix se o banco central responder forbidden`() {
        val dadosCliente = getDadosCliente()
        Mockito.`when`(itauClient.buscarCliente(clienteId))
            .thenReturn(HttpResponse.ok(dadosCliente))

//        Mockito.`when`(bcCliente.remover(chavePrincipal, DeletePixKeyRequest(chavePrincipal)))
//            .thenReturn(HttpResponse.status(HttpStatus.FORBIDDEN))
        doReturn(HttpResponse.status<DeletePixKeyResponse>(HttpStatus.FORBIDDEN))
            .`when`(bcCliente).remover(chavePrincipal, DeletePixKeyRequest(chavePrincipal))

        val chavePixRequest = RemoverChaveRequest.newBuilder()
            .setPixId(chavePixId)
            .setClienteId(clienteId)
            .build()

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.remover(chavePixRequest)
        }

        with(error){
            assertEquals(Status.fromCodeValue(403).code, status.code)
            assertEquals("Servidor não autorizou a ação", status.description)
        }
    }

    @Test
    fun `não deve remover caso o pixId nao foi localizado`() {
        val chavePixRequest = RemoverChaveRequest.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setClienteId(clienteId)
            .build()

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.remover(chavePixRequest)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("PIX não localizado no sistema", status.description)
        }
    }

    @Test
    fun `não deve remover caso o cliente não seja localizado`() {
        val chavePixRequest = RemoverChaveRequest.newBuilder()
            .setPixId(chavePixId)
            .setClienteId(UUID.randomUUID().toString())
            .build()

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.remover(chavePixRequest)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente não localizado no sistema do Itau", status.description)
        }
    }

    @Test
    fun `não deve remover caso o pix nao pertenca ao cliente`() {
        val chavePixRequest = RemoverChaveRequest.newBuilder()
            .setPixId(chavePixId)
            .setClienteId(clienteId2)
            .build()

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.remover(chavePixRequest)
        }

        with(error){
            assertEquals(Status.PERMISSION_DENIED.code, status.code)
            assertEquals("Conta PIX não pertence a este usuario", status.description)
        }
    }

    @Test
    fun `erro ao buscar a chave com pixId e clienteId`() {
        val chavePixRequest = RemoverChaveRequest.newBuilder()
            .setPixId(chavePixId)
            .setClienteId(clienteId2)
            .build()

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.remover(chavePixRequest)
        }

        with(error){
            assertEquals(Status.fromCodeValue(500), status.code)
            assertEquals("Erro ao acessar o servidor", status.description)
        }
    }

    @Test
    fun `Argumentos invalidos`() {
        val chavePixRequest = RemoverChaveRequest.newBuilder().build()

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.remover(chavePixRequest)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados invalidos", status.description)
        }
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

    @MockBean(BancoCentralCliente::class)
    fun bancoCentralMock(): BancoCentralCliente{
        return Mockito.mock(BancoCentralCliente::class.java)
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