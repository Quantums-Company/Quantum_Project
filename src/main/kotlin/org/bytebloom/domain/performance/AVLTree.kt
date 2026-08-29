package org.bytebloom.domain.performance

class AVLTree {

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

        when {
            value < node.value -> {
                node.left = insert(node.left, value)
            }

            value > node.value -> {
                node.right = insert(node.right, value)
            }

            else -> return node
        }

        updateHeight(node)

        return rebalance(node)
    }

    private fun updateHeight(node: Node<String>) {
        node.height = 1 + maxHeight(node.left, node.right)
    }

    private fun maxHeight(
        left: Node<String>?,
        right: Node<String>?
    ): Int {
        return maxOf(
            height(left),
            height(right)
        )
    }

    private fun height(node: Node<String>?): Int =
        node?.height ?: 0

    private fun balanceFactor(node: Node<String>): Int =
        height(node.left) - height(node.right)

    private fun rebalance(node: Node<String>): Node<String> {

        val balance = balanceFactor(node)

        if (balance > 1) {
            if (balanceFactor(node.left!!) < 0) {
                node.left = rotateLeft(node.left!!)
            }

            return rotateRight(node)
        }

        if (balance < -1) {
            if (balanceFactor(node.right!!) > 0) {
                node.right = rotateRight(node.right!!)
            }

            return rotateLeft(node)
        }

        return node
    }

    private fun rotateRight(node: Node<String>): Node<String> {

        val newRoot = node.left!!
        val movedSubtree = newRoot.right

        newRoot.right = node
        node.left = movedSubtree

        updateHeight(node)
        updateHeight(newRoot)

        return newRoot
    }

    private fun rotateLeft(node: Node<String>): Node<String> {

        val newRoot = node.right!!
        val movedSubtree = newRoot.left

        newRoot.left = node
        node.right = movedSubtree

        updateHeight(node)
        updateHeight(newRoot)

        return newRoot
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