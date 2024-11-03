/*
 * @(#) JSONSpringAutoConfiguration.kt
 *
 * kjson-spring3  Spring Boot 3 JSON message converter for kjson
 * Copyright (c) 2023 Peter Wall
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

package io.kjson.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered
import org.springframework.http.converter.HttpMessageConverter

import io.kjson.JSONConfig
import net.pwall.log.Level
import net.pwall.log.LoggerFactory

/**
 * Auto-configuration class for `kjson` and Spring Boot 3.
 *
 * @author  Peter Wall
 */
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Suppress("unused")
open class JSONSpringAutoConfiguration {

    @Bean
    open fun createJSONHttpMessageConverter(
        @Autowired(required = false) jsonConfig: JSONConfig?,
        @Autowired(required = false) jsonLogFactory: LoggerFactory<*>?,
        @Autowired(required = false) jsonLogName: String?,
        @Autowired(required = false) jsonLogLevel: Level?,
        @Autowired(required = false) jsonLogExclude: Collection<String>?,
    ): HttpMessageConverter<Any> {
        return JSONSpring(jsonConfig, jsonLogFactory, jsonLogName, jsonLogLevel, jsonLogExclude)
    }

}
