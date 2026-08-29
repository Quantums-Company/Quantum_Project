package org.bytebloom.domain.performance

class BST {

    private var root: Node<String>? = null

    fun insert(value: String) {
        root = insert(root, value)
    }

    private fun insert(
        node: Node<String>?,
        value: String
    ): Node<String> {

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

    fun search(value: String): Int {

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