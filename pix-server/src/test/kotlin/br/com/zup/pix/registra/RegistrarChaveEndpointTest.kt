package br.com.zup.pix.registra

import br.com.zup.pix.PixServerServiceGrpc
import br.com.zup.pix.RegistrarChaveRequest
import br.com.zup.pix.TipoChave
import br.com.zup.pix.TipoConta
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
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

    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixServerServiceGrpc.PixServerServiceBlockingStub?{
            return PixServerServiceGrpc.newBlockingStub(channel)
        }
    }
}