package br.com.zup.pix.externo.itau

import javax.persistence.Embeddable


@Embeddable
class ContaAssociada(
    val instuicao: String,
    val nomeTitular: String,
    val cpfTitular: String,
    val agencia: String,
    val numeroConta: String
) {
    companion object{
        val ITAU_UNIBANCO_ISPB = "60701190"
    }
}
