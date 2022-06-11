package com.mvi.sample

import org.junit.Assert.assertEquals
import org.junit.Test

class Arrays {

    @Test
    fun main() {
        val input = listOf(
            listOf(2, 3, 1, 5).toIntArray(),
            listOf(2, 3, 1, 4, 8, 5, 6, 10, 9).toIntArray(),
        )

        val expected = listOf(4, 7)

        val results = input.map { solution(it) }

        assertEquals(expected, results)
    }

    fun solution(array: IntArray): Int {
        var result: Int = -1
        array.sorted().reduceWhile { left, right ->
            if (right - left != 1) {
                breakFlow = true
                result = left + 1
            }
            right
        }
        return result

    }

    fun <T> List<T>.reduceWhile(onNext: StoppableReduce.(T, T) -> T): T {
        val originalIterator = iterator()
        val stoppableReduce = StoppableReduce()
        val iterable = object : Iterable<T> {
            override fun iterator(): Iterator<T> = object : Iterator<T> {
                override fun hasNext() = !stoppableReduce.breakFlow && originalIterator.hasNext()
                override fun next(): T = originalIterator.next()
            }
        }
        return iterable.reduce { acc, t -> stoppableReduce.onNext(acc, t) }
    }

    class StoppableReduce {
        var breakFlow = false
    }


}