package br.com.zup.pix.externo.itau

class DadosClienteResponse(
    val id: String,
    val nome: String,
    val cpf: String,
    val instituicao: InstituicaoResponse
) {

}
