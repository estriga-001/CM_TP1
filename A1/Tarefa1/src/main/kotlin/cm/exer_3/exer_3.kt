package org.example.cm.exer_3

fun main(){
    // altura inicial = 100
    val h0 = 100.0
    val hn = generateSequence(h0) {it* 0.6}

    print(hn.takeWhile{it >= 1.0}.take(15).map{"%.2f".format(it)}.toList())
}