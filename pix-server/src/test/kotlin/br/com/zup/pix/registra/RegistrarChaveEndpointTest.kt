package br.com.zup.pix.registra

import br.com.zup.pix.PixServerServiceGrpc
import br.com.zup.pix.RegistrarChaveRequest
import br.com.zup.pix.TipoChave
import br.com.zup.pix.TipoConta
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest
internal class RegistrarChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: PixServerServiceGrpc.PixServerServiceBlockingStub
){
    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `deve registrar uma nova chave pix`(){
        val chaveRequest = RegistrarChaveRequest.newBuilder()
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
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

    @Test
    fun `nao deve cadastrar caso a chave ja foi cadastrada`(){
        val chave = "02467781054"
        val chaveRequest = RegistrarChaveRequest.newBuilder()
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
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
        val chaveRequest = RegistrarChaveRequest.newBuilder()
            .setClienteId("c56dfef3-7902-44fc-84e3-a2cefb157891")//modifiquei alguns valores
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

    @Test
    fun `nao deve cadastrar algum dado passado for invalido`(){
        val chaveRequest = RegistrarChaveRequest.newBuilder().build()

        val error = assertThrows<StatusRuntimeException>{
            grpcClient.registrar(chaveRequest)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados invalidos", status.description)
        }
    }

    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixServerServiceGrpc.PixServerServiceBlockingStub?{
            return PixServerServiceGrpc.newBlockingStub(channel)
        }
    }
}