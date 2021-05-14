package br.com.zup.pix.contaPix

import br.com.zup.pix.contaPix.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, UUID> {
    fun existsByChave(chave: String?): Boolean

    fun existsByIdAndClienteId(pixId: UUID, clienteId: UUID): Boolean

    fun findByIdAndClienteId(pixId: UUID, clienteId: UUID): Optional<ChavePix>


}