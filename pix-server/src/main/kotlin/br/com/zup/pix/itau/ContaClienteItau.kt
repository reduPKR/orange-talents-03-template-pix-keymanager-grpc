package br.com.zup.pix.itau

import io.micronaut.http.HttpResponse
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue

@Client("\${itau.contas.url}")
interface ContaClienteItau {
    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscarContaPorTipo(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DadosContaResponse>

    @Get("/api/v1/clientes/{clienteId}")
    fun buscarCliente(@PathVariable clienteId: String): HttpResponse<DadosClienteResponse>
}
