package org.bytebloom.domain.tree.binary

import org.bytebloom.domain.tree.Node

class AVLTree<T : Comparable<T>> {

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

    private fun updateHeight(node: Node<T>) {
        node.height = 1 + maxHeight(node.left, node.right)
    }

    private fun maxHeight(
        left: Node<T>?,
        right: Node<T>?
    ): Int {
        return maxOf(
            height(left),
            height(right)
        )
    }

    private fun height(node: Node<T>?): Int =
        node?.height ?: 0

    private fun balanceFactor(node: Node<T>): Int =
        height(node.left) - height(node.right)

    private fun rebalance(node: Node<T>): Node<T> {

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

    private fun rotateRight(node: Node<T>): Node<T> {

        val newRoot = node.left!!
        val movedSubtree = newRoot.right

        newRoot.right = node
        node.left = movedSubtree

        updateHeight(node)
        updateHeight(newRoot)

        return newRoot
    }

    private fun rotateLeft(node: Node<T>): Node<T> {

        val newRoot = node.right!!
        val movedSubtree = newRoot.left

        newRoot.left = node
        node.right = movedSubtree

        updateHeight(node)
        updateHeight(newRoot)

        return newRoot
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