package br.com.zup.pix.itau

data class DadosContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    fun toModel(): ContaAssociada {
        return ContaAssociada(
            this.instituicao.nome,
            this.titular.nome,
            this.titular.cpf,
            this.agencia,
            this.numero
        )
    }
}
