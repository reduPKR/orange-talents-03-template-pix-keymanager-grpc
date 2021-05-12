package br.com.zup.pix.registra

import br.com.zup.pix.itau.ContaClienteItau
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid

class ChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ContaClienteItau
) {

    @Transactional
    fun registrar(@Valid chavePixRequest: ChavePixRequest): ChavePix {
        
    }

}
