package br.com.zup.pix.contaPix.consulta

import br.com.zup.pix.*
import br.com.zup.pix.contaPix.ChavePix
import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.externo.bancoCentral.BancoCentralCliente
import br.com.zup.pix.externo.itau.ContaAssociada
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ConsultaChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: PixServerConsultarServiceGrpc.PixServerConsultarServiceBlockingStub
){
    @Inject
    lateinit var bcCliente: BancoCentralCliente

    lateinit var chavePix1: ChavePix
    lateinit var chavePix2: ChavePix
    lateinit var chavePix3: ChavePix

    @BeforeEach
    fun setUp() {
        repository.deleteAll()

        val chave1 = ChavePix(
            UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890"),
            br.com.zup.pix.contaPix.TipoChave.CPF,
            "02467781054",
            br.com.zup.pix.contaPix.TipoConta.CONTA_CORRENTE,
            ContaAssociada(
                "ITAÚ UNIBANCO S.A.",
                "Rafael",
                "02467781054",
                "001",
                "1234"
            )
        )

        val chave2 = ChavePix(
            UUID.fromString("5260263c-a3c1-4727-ae32-3bdb2538841b"),
            br.com.zup.pix.contaPix.TipoChave.CPF,
            "86135457004",
            br.com.zup.pix.contaPix.TipoConta.CONTA_POUPANCA,
            ContaAssociada(
                "ITAÚ UNIBANCO S.A.",
                "Eduardo",
                "86135457004",
                "001",
                "2345"
            )
        )

        val chave3 = ChavePix(
            UUID.fromString("bc35591d-b547-4151-a325-4a9d2cd19614"),
            br.com.zup.pix.contaPix.TipoChave.CPF,
            "64370752019",
            br.com.zup.pix.contaPix.TipoConta.CONTA_CORRENTE,
            ContaAssociada(
                "ITAÚ UNIBANCO S.A.",
                "Lucas",
                "64370752019",
                "001",
                "3456"
            )
        )

        chavePix1 = repository.save(chave1)
        chavePix2 = repository.save(chave2)
        chavePix3 = repository.save(chave3)
    }

    @Test
    fun `deve retornar dados da consulta pelo pixId`(){

        val chaveRequest = ConsultarChavePixRequest.newBuilder()
            .setPixId(
                ConsultarChavePixRequest.FiltroPorPixId.newBuilder()
                .setClienteId(chavePix2.clienteId.toString())
                .setPixId(chavePix2.id.toString())
                    .build()
            ).build()

        val retorno = grpcClient.consultar(chaveRequest)

        assertEquals(chavePix2.clienteId.toString(), retorno.clienteId)
        assertEquals(chavePix2.id.toString(), retorno.pixId)
        assertEquals(br.com.zup.pix.TipoChave.CPF, retorno.chave.tipoChave)
        assertEquals(chavePix2.chave, retorno.chave.chave)
        assertEquals(br.com.zup.pix.TipoConta.CONTA_POUPANCA, retorno.chave.conta.tipoConta)
        assertEquals(chavePix2.conta.instuicao, retorno.chave.conta.instituicao)
        assertEquals(chavePix2.conta.nomeTitular, retorno.chave.conta.nomeTitular)
        assertEquals(chavePix2.conta.cpfTitular, retorno.chave.conta.cpfTitular)
        assertEquals(chavePix2.conta.agencia, retorno.chave.conta.agencia)
        assertEquals(chavePix2.conta.numeroConta, retorno.chave.conta.numeroConta)
    }

    @Test
    fun `nao deve retornar dados da consulta pelo pixId se o clientId nao for encontrado`(){}

    @Test
    fun `nao deve retornar dados da consulta pelo pixId se o pixId nao for encontrado`(){}

    @Test
    fun `nao deve retornar dados da consulta pelo pixId se o clientId e o pixId nao forem da mesma chave`(){}

    @Test
    fun `deve retornar dados na consulta pela chave`(){}

    @Test
    fun `não deve retornar dados na consulta pela chave`(){}

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
    }
}