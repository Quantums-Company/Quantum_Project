package org.bytebloom.domain.tree.binary

import org.bytebloom.domain.tree.Node

class BST<T : Comparable<T>> {

    private var root: Node<T>? = null

    fun insert(value: T) {
        root = insert(root, value)
    }

    private fun insert(
        node: Node<T>?,
        value: T
    ): Node<T> {

        if (node == null) {
            return Node(value)
        }

        if (value < node.value) {
            node.left = insert(node.left, value)
        } else if (value > node.value) {
            node.right = insert(node.right, value)
        }

        return node
    }

    fun search(value: T): Int {

        var current = root
        var steps = 0

        while (current != null) {
            steps++

            when {
                value == current.value -> return steps
                value < current.value -> current = current.left
                else -> current = current.right
            }
        }

        return steps
    }
}