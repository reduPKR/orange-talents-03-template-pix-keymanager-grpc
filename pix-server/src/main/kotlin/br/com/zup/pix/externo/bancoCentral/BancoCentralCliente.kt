package br.com.zup.pix.externo.bancoCentral

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.contas.url}")
interface BancoCentralCliente {
    @Post("/api/v1/pix/keys")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun cadastrar(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete("/api/v1/pix/keys/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    fun remover(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>
}