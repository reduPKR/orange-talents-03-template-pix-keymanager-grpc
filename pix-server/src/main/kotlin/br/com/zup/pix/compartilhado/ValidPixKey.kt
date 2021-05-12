package br.com.zup.pix.compartilhado

import br.com.zup.pix.registra.ChavePixRequest
import br.com.zup.pix.registra.TipoChave
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "Chave invalida (\${validated.tipoChave})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidPixKeyValidator: ConstraintValidator<ValidPixKey, ChavePixRequest> {
    override fun isValid(
        value: ChavePixRequest?,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: ConstraintValidatorContext?
    ): Boolean {
        return value?.tipoChave!!.validar(value.chave)
    }

}


