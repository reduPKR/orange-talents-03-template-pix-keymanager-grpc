package br.com.zup.pix.registra

import br.com.zup.pix.contaPix.TipoChave
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest{
    @Nested
    inner class CPF{
        @Test
        fun `deve ser valido se o cpf for valido`(){
            with(TipoChave.CPF){
                assertTrue(validar("53768715760"))
            }
        }

        @Test
        fun `nao deve ser valido se o cpf estiver incorreto`(){
            with(TipoChave.CPF){
                assertFalse(validar("53768715761"))
            }
        }

        @Test
        fun `nao deve ser valido se o cpf estiver com quantidade de caracteres diferente de 11`(){
            with(TipoChave.CPF){
                assertFalse(validar("537687157601"))
            }
        }

        @Test
        fun `nao deve ser valido se o cpf nao for informado`(){
            with(TipoChave.CPF){
                assertFalse(validar(null))
                assertFalse(validar(""))
            }
        }
    }

    @Nested
    inner class CELULAR{
        @Test
        fun `deve ser valido se o celular possuir 14 numeros`(){
            with(TipoChave.CELULAR){
                assertTrue(validar("+5585988714077"))
            }
        }

        @Test
        fun `nao deve ser valido se o celular nao apenas numeros`(){
            with(TipoChave.CELULAR){
                assertFalse(validar("+558598aa14077"))
            }
        }
    }

    @Nested
    inner class EMAIL{
        @Test
        fun `email deve ser valido`(){
            with(TipoChave.EMAIL){
                assertTrue(validar("rafael@gmail.com"))
                assertTrue(validar("rafael.aparecido@gmail.com"))
                assertTrue(validar("rafael@gmail.com.br"))
                assertTrue(validar("rafael.aparecido@gmail.com.br"))
            }
        }

        @Test
        fun `email nao deve ser valido`(){
            with(TipoChave.EMAIL){
                assertFalse(validar("rafael@.com"))
                assertFalse(validar("rafael.aparecido@@gmail.com"))
                assertFalse(validar("rafael@gmail.com..br"))
                assertFalse(validar("rafael.aparecido@_gmail.com.br"))
            }
        }
    }

    @Nested
    inner class ALEATORIA{
        @Test
        fun `deve ser valido se nao for passado uma chave`(){
            with(TipoChave.ALEATORIA){
                assertTrue(validar(null))
                assertTrue(validar(""))
            }
        }

        @Test
        fun `deve ser invalido se for passada uma chave`(){
            with(TipoChave.ALEATORIA){
                assertFalse(validar("123123123"))
            }
        }
    }
}