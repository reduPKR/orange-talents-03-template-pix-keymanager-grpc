package br.com.zup.pix.registra

import br.com.zup.pix.compartilhado.ValidadorCPF
import br.com.zup.pix.compartilhado.ValidadorEmail

enum class TipoChave {
    UNKNOWN_CHAVE{
        override fun validar(chave: String?): Boolean {
            return false
        }
    },
    CPF{
        override fun validar(chave: String?): Boolean {
            if(chave.isNullOrBlank())
                return false
            if (!chave.matches("[0-9]+".toRegex()))
                return false
            return ValidadorCPF(chave).validar()
        }
    },
    CELULAR{
        override fun validar(chave: String?): Boolean {
            if(chave.isNullOrBlank())
                return false
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL{
        override fun validar(chave: String?): Boolean {
            if(chave.isNullOrBlank())
                return false
            return ValidadorEmail(chave).validar()
        }
    },
    ALEATORIA{
        override fun validar(chave: String?): Boolean {
            println(chave.isNullOrBlank())
            return chave.isNullOrBlank()
        }
    };

    abstract fun validar(chave: String?): Boolean
}