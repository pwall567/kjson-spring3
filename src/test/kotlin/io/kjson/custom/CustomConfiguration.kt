/*
 * @(#) CustomConfiguration.kt
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

package io.kjson.custom

import java.util.UUID

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import io.kjson.JSON.asObject
import io.kjson.JSON.asString
import io.kjson.JSONConfig
import io.kjson.JSONObject
import io.kjson.spring.test.RequestData
import io.kjson.spring.test.ResponseData

@Configuration
@Suppress("unused")
open class CustomConfiguration {

    @Bean open fun jsonConfig(): JSONConfig {
        return JSONConfig {
            toJSON<ResponseData> {
                JSONObject.build {
                    add("D", it.date.toString())
                    add("X", it.extra)
                }
            }
            fromJSON { value ->
                value.asObject.let {
                    RequestData(UUID.fromString(it["I"].asString), it["N"].asString)
                }
            }
        }
    }

}
