package br.com.zup.pix.itau

import io.micronaut.http.client.annotation.Client
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import java.net.http.HttpResponse

@Client("\${itau.contas.url}")
interface ContaClienteItau {
    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun BuscarContaPorTipo(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DadosContaResponse>
}
