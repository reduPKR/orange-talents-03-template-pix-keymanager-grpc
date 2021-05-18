package br.com.zup.pix.compartilhado

import br.com.zup.pix.externo.itau.InstituicaoResponse
import io.micronaut.core.annotation.Introspected

class ListaInstituicoes {
   companion object{
       val lista: MutableList<InstituicaoResponse> = mutableListOf(
           InstituicaoResponse("Banco 1", "01782364"),
           InstituicaoResponse("Banco 2", "82746534"),
           InstituicaoResponse("ITAÚ UNIBANCO S.A.", "60701190"),
           InstituicaoResponse("Banco 3", "72834664"),
           InstituicaoResponse("Banco 4", "82937423")
       )

        fun encontarISPB(isbp: String): String{
            val retorno = lista.filter { it.ispb == isbp }

            if(retorno.isEmpty())
                return "Banco desconhecido"
            return retorno.get(0).nome;
        }
    }

}
