package org.bytebloom.domain.performance

class Node<T : Comparable<T>>(
    val value: T
) {
    var left: Node<T>? = null
    var right: Node<T>? = null
    var height: Int = 1
}