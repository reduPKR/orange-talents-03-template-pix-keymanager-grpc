package br.com.zup.pix.externo.bancoCentral


import br.com.zup.pix.contaPix.TipoChave
import java.lang.IllegalArgumentException

enum class KeyType(val tipoChave: TipoChave?) {
    CPF(TipoChave.CPF),
    CNPJ(null),
    PHONE(TipoChave.CELULAR),
    EMAIL(TipoChave.EMAIL),
    RANDOM(TipoChave.ALEATORIA);

    companion object{
        private val mapping = KeyType.values().associateBy(KeyType::tipoChave)

        fun converter(tipoChave: TipoChave): KeyType{
            return mapping[tipoChave] ?: throw IllegalArgumentException("Chave PIX ilegal")
        }
    }
}
