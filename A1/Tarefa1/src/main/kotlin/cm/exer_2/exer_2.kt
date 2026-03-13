package org.example.cm.exer_2

// calculadora

fun main(){

    println("Calculadora\n")

    try {
        println("Escolha a operação: +,-,*,/, &&, ||, !, shl, shr")
        val sinal = readln().trim()

        when(sinal) {
            "+", "-", "*", "/" -> {
                println("Introduza o primeiro numero: ")
                val a = readln().toDouble()

                print("Introduza o segundo numero: ")
                val b = readln().toDouble()

                val resultado = when (sinal) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> {
                        if (b == 0.0) throw ArithmeticException("Divisão por zero.")
                        a / b
                    }

                    else -> throw IllegalArgumentException("Operação inválida.")
                }
                // resultado
                println("Resultado: ")
                // retorna com duas casas decimais
                println("Decimal: %.2f".format(resultado))
                // retonra em hex
                println("Hexadecimal: ${resultado.toLong().toString(16)}")
            }
            "&&", "||" -> {
                println("Primeiro bool ('true' ou 'false'): ")
                val bo = readln().trim().toBooleanStrict()

                println("Segundo bool ('true' ou 'false'): ")
                val bo2 = readln().trim().toBooleanStrict()

                when(sinal) {
                    "&&" -> println("AND: ${bo && bo2}")
                    "||" -> println("OR: ${bo || bo2}")

                    else -> throw IllegalArgumentException("Esse sinal não existe.")
                }
            }
            "!" -> {
                println("Introduz um bool ('true' ou 'false'): ")
                val bo3 = readln().trim().toBooleanStrict()

                when(sinal){
                    "!" -> println("Negação: ${!bo3}")
                    else -> throw IllegalArgumentException("Esse sinal não existe.")
                }
            }
            "shl", "shr" -> {
                println("Introduz um numero inteiro: ")
                val n1 = readln().trim().toInt()
                println("Introduz outro numero inteiro: ")
                val n2 = readln().trim().toInt()

                if(n2 < 0) throw IllegalArgumentException("O deslocamento tem de ser >= 0.")

                when(sinal) {
                    "shl" -> println("Deslocamento a esquerda: ${n1 shl n2}")
                    "shr" -> println("Deslocamento a direita: ${n1 shr n2}")
                    else -> throw IllegalArgumentException("Operação inválida.")
                }
            }
        }
    }catch(e: IllegalArgumentException){
        println("Input inválido. $e")
    }
    catch (e: ArithmeticException){
        println("O denominador tem que ser diferente de 0. $e")
    }
}