/*
 * @(#) TestController.kt
 *
 * kjson-spring3  Spring Boot 3 JSON message converter for kjson
 * Copyright (c) 2022, 2023 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.kjson.spring.test

import java.time.LocalDate

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Suppress("unused")
class TestController {

    @GetMapping("/testendpoint")
    fun getDummyData(): ResponseData {
        return ResponseData(
            date = LocalDate.of(2022, 7, 1),
            extra = "Hello!",
        )
    }

    @PostMapping("/testendpoint")
    fun dummyPost(
        @RequestBody requestData: RequestData,
    ): ResponseData {
        return ResponseData(
            date = LocalDate.of(2022, 7, 4),
            extra = requestData.id.toString(),
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handleException(e: Exception): String {
        e.printStackTrace()
        return "\"ERROR\""
    }

}
