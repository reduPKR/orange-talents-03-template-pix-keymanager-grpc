package br.com.zup.pix.contaPix.lista

import br.com.zup.pix.ListarChavePixRequest
import br.com.zup.pix.PixServerListarServiceGrpc
import br.com.zup.pix.contaPix.ChavePix
import br.com.zup.pix.contaPix.ChavePixRepository
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.externo.itau.ContaAssociada
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
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
internal class ListarChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: PixServerListarServiceGrpc.PixServerListarServiceBlockingStub
){
    lateinit var chavePix1: ChavePix
    lateinit var chavePix2: ChavePix
    lateinit var chavePix3: ChavePix
    lateinit var chavePix4: ChavePix

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
            UUID.fromString("5260263c-a3c1-4727-ae32-3bdb2538841b"),
            br.com.zup.pix.contaPix.TipoChave.EMAIL,
            "reduardo@gmail.com",
            br.com.zup.pix.contaPix.TipoConta.CONTA_POUPANCA,
            ContaAssociada(
                "ITAÚ UNIBANCO S.A.",
                "Eduardo",
                "86135457004",
                "001",
                "2345"
            )
        )

        val chave4 = ChavePix(
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
        chavePix4 = repository.save(chave4)
    }

    @Test
    fun `deve retornar a lista`(){
        val request = ListarChavePixRequest.newBuilder()
            .setClienteId(chavePix2.clienteId.toString())
            .build()

        val response = grpcClient.listar(request)
        assertEquals(2, response.listaChavesCount)

        var chave1 = response.listaChavesList[0]
        var chave2 = response.listaChavesList[1]
        if(chavePix2.id != UUID.fromString(response.listaChavesList[0].pixId)){
            chave2 = response.listaChavesList[0]
            chave1 = response.listaChavesList[1]
        }


        assertEquals(chavePix2.id, UUID.fromString(chave1.pixId))
        assertEquals(chavePix2.tipoChave.name,chave1.tipoChave.name)
        assertEquals(chavePix2.chave,chave1.chave)
        assertEquals(chavePix2.tipoConta.name,chave1.tipoConta.name)

        assertEquals(chavePix3.id, UUID.fromString(chave2.pixId))
        assertEquals(chavePix3.tipoChave.name,chave2.tipoChave.name)
        assertEquals(chavePix3.chave,chave2.chave)
        assertEquals(chavePix3.tipoConta.name,chave2.tipoConta.name)
    }

    @Test
    fun `deve retornar lista vazia se o cliente nao for encontrado`(){
        val request = ListarChavePixRequest.newBuilder()
            .setClienteId(UUID.randomUUID().toString())
            .build()

        val response = grpcClient.listar(request)
        assertEquals(0, response.listaChavesCount)
    }

    @Test
    fun `deve retornar erro se nao for passado um cliente`(){
        val request = ListarChavePixRequest.newBuilder()
            .build()

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.listar(request)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Nenhum cliente informado", status.description)
        }
    }

    @Factory
    class Client{
        @Singleton
        fun blockingStup(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
        :PixServerListarServiceGrpc.PixServerListarServiceBlockingStub{
            return PixServerListarServiceGrpc.newBlockingStub(channel)
        }
    }
}