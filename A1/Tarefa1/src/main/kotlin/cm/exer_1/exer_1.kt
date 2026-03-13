package org.example.cm.exer_1

fun main(){

    println("Exercicio 1:")
    /*
    * a) utilização do construtor intarray
    * o construtor recebe o tamanho (50) e uma lambda que inicializa
    * cada posição cmo base no indice
    *
    * como o indice começa em 0, usamos o index + 1
    * para gerar os quadrados de 1^2 até 50
    * */
    val quadrados = IntArray(50) { index ->
        val n = index + 1
        n * n
    }

    // joinToString() é necessario porque arrays nao imprimem o conteudo por defeito
    println(quadrados.joinToString())

    // outra maneira range e map
    println("Segunda maneira: ")
    /*
    * b) Utilização de um range (1..50) e da função map().
    *
    * O range gera os numeros de 1 até 50
    * a função map transforma cada elemento no seu quadrado
    *
    * Nota: map devolve uma List<Int>, nao um intarray
    * */
    val quads = (1..50).map {n ->
        n*n
    }
    // list já tem o toString() implementado corretamente
    println(quads)

    // terceira maneira array constructor
    println("Terceira maneira: ")
    /*
    * c) Utilização do construtor array
    *
    * funciona de forma semelhante ao IntArray, mas cria um Array<int>
    ou seja, usa objetos em vez de primitivos
    *
    * Também aqui usamos (index + 1) porque o indice começa em 0*/
    val quads2 = Array(50) { index ->
        val n = index + 1
        n * n
    }
    println(quads2.joinToString())
}