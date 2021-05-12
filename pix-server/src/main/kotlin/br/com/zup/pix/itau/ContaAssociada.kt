package br.com.zup.pix.itau

import javax.persistence.Embeddable


@Embeddable
class ContaAssociada(
    val instuicao: String,
    val nomeTitular: String,
    val cpfTitular: String,
    val agencia: String,
    val numeroConta: String
) {

}
