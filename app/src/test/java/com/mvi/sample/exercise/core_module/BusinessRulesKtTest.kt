package com.mvi.sample.exercise.core_module

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * Task is to call the web service,
 * show it as a table without using any 3rd party library
 * or online help, only the official documentation
 * (could be Android Studio with screen sharing or Yandex
 * Code, whichever is convenient for the interviewee)
Example of data to use: curl -H 'Accept: application/vnd.github.v3.text-match+json' 'https://api.github.com/search/issues?q=windows+label:bug+language:python+state:open&sort=created&order=asc'
 */


/**
 * title1     number1
 * title2     number2
 */

class BusinessRulesKtTest {

    @Test
    fun `requestTable() then return a Table`() {
        runBlocking {
            val expected = listOf(
                TableRow("A", 1),
                TableRow("B", 2),
            )

            val repository = object : TableRepository {
                override suspend fun getTable() = expected
            }
            val result = requestTable(repository)

            assertEquals(expected, result)
        }

    }

    @Test(expected = UnsupportedOperationException::class)
    fun `requestTable() with error then throw an Exception`() {
        runBlocking {
            val repository = object : TableRepository {
                override suspend fun getTable() = throw UnsupportedOperationException()
            }
            requestTable(repository)

        }
    }


}