package br.com.zup.pix.validador

class ValidadorCPF(val chave: String) {
    fun validar(): Boolean {
        val cpf = chave.replace(".", "").replace("-", "")

        if(cpf.length != 11)
            return false

        var digito10 = calcularDigito(cpf, 10)
        var digito11 = calcularDigito(cpf, 11)

        if (digito10 == (cpf.get(9) -48).toInt() && digito11 == (cpf.get(10) -48).toInt())
            return true
        return false
    }

    private fun calcularDigito(cpf: String, pesoAux: Int): Int {
        var soma = 0
        var peso = pesoAux
        val limite = pesoAux -2

        for (i in 0..limite) {
            soma += (((cpf.get(i) - 48).toInt()) * peso)
            peso--
        }

        var resto = 11 - (soma % 11)
        if (resto == 10 || resto == 11)
            return 0
        return resto
    }
}
