package br.com.zup.pix.contaPix.consulta

import br.com.zup.pix.ConsultarChavePixResponse
import br.com.zup.pix.TipoChave
import br.com.zup.pix.TipoConta
import com.google.protobuf.Timestamp
import java.time.ZoneId

class ConsultarChavePixConverter {
    fun converter(chavePixInfo: ChavePixInfo): ConsultarChavePixResponse {
        return ConsultarChavePixResponse.newBuilder()
            .setClienteId(chavePixInfo.clientId?.toString() ?: "")
            .setPixId(chavePixInfo.pixId?.toString() ?: "")
            .setChave(
                ConsultarChavePixResponse.ChavePix.newBuilder()
                    .setTipoChave(TipoChave.valueOf(chavePixInfo.tipoChave.name))
                    .setChave(chavePixInfo.chave)
                    .setConta(ConsultarChavePixResponse.ChavePix.ContaInfo.newBuilder()
                        .setTipoConta(TipoConta.valueOf(chavePixInfo.tipoConta.name))
                        .setInstituicao(chavePixInfo.conta.instuicao)
                        .setNomeTitular(chavePixInfo.conta.nomeTitular)
                        .setCpfTitular(chavePixInfo.conta.cpfTitular)
                        .setAgencia(chavePixInfo.conta.agencia)
                        .setNumeroConta(chavePixInfo.conta.numeroConta)
                        .build()
                    )
                    .setCriadoEm(
                        chavePixInfo.registradaEm.let {
                            val criadoEm = it.atZone(ZoneId.of("UTC")).toInstant()
                            Timestamp.newBuilder()
                                .setSeconds(criadoEm.epochSecond)
                                .setNanos(criadoEm.nano)
                                .build()
                        })
            ).build()
    }

}
