package br.com.zup.pix.externo.bancoCentral

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.contas.url}")
interface BancoCentralCliente {
    @Post(
        "/api/v1/pix/keys",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun cadastrar(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete(
        "/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML]
    )
    fun remover(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>
}