package br.com.zup.pix.contaPix.consulta

import br.com.zup.pix.*
import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.contaPix.registra.RegistrarChaveEndpoint
import br.com.zup.pix.contaPix.registra.toModel
import br.com.zup.pix.externo.bancoCentral.BancoCentralCliente
import io.grpc.ManagedChannel
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ConsultaChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: PixServerConsultarServiceGrpc.PixServerConsultarServiceBlockingStub,
    val grpcRegistrar: PixServerRegistrarServiceGrpc.PixServerRegistrarServiceBlockingStub
){
    @Inject
    lateinit var bcCliente: BancoCentralCliente

    lateinit var chavePix1: ChaveRegistradaResponse
    lateinit var chavePix2: ChaveRegistradaResponse
    lateinit var chavePix3: ChaveRegistradaResponse

    @BeforeEach
    fun setUp() {
        repository.deleteAll()

        val chave1 = RegistrarChaveRequest.newBuilder()
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.valueOf("CPF"))
            .setChave("02467781054")
            .setTipoConta(TipoConta.valueOf("CONTA_CORRENTE"))
            .build()

        val chave2 = RegistrarChaveRequest.newBuilder()
            .setClienteId("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .setTipoChave(TipoChave.valueOf("CPF"))
            .setChave("86135457004")
            .setTipoConta(TipoConta.valueOf("CONTA_POUPANCA"))
            .build()

        val chave3 = RegistrarChaveRequest.newBuilder()
            .setClienteId("bc35591d-b547-4151-a325-4a9d2cd19614")
            .setTipoChave(TipoChave.valueOf("CPF"))
            .setChave("64370752019")
            .setTipoConta(TipoConta.valueOf("CONTA_CORRENTE"))
            .build()


        chavePix1 = grpcRegistrar.registrar(chave1)
        chavePix2 = grpcRegistrar.registrar(chave2)
        chavePix3 = grpcRegistrar.registrar(chave3)
    }

    @Test
    fun `deve retornar dados da consulta pelo pixId`(){

        
    }

    @MockBean(BancoCentralCliente::class)
    fun bancoCentralMock(): BancoCentralCliente?{
        return Mockito.mock(BancoCentralCliente::class.java)
    }

    @Factory
    class Clients{
        @Singleton
        fun blockingStup(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
        :PixServerConsultarServiceGrpc.PixServerConsultarServiceBlockingStub{
            return PixServerConsultarServiceGrpc.newBlockingStub(channel)
        }

        @Singleton
        fun registrar(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
        :PixServerRegistrarServiceGrpc.PixServerRegistrarServiceBlockingStub{
            return PixServerRegistrarServiceGrpc.newBlockingStub(channel)
        }
    }
}