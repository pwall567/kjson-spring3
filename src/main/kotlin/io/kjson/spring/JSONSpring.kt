/*
 * @(#) JSONSpring.kt
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

package io.kjson.spring

import java.io.Reader
import java.io.Writer
import java.lang.reflect.Type

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter
import org.springframework.stereotype.Service

import io.kjson.JSON.appendJSONValue
import io.kjson.JSON.elidedValue
import io.kjson.JSONConfig
import io.kjson.JSONException
import io.kjson.JSONSerializer
import io.kjson.JSONStreamer
import io.kjson.JSONStringify.appendJSON
import io.kjson.fromJSONValue
import net.pwall.log.Level
import net.pwall.log.Logger
import net.pwall.log.LoggerFactory

/**
 * Spring message converter to convert messages to and from JSON using the [kjson](https://github.com/pwall567/kjson)
 * library.
 *
 * @author  Peter Wall
 */
@Service
@Suppress("unused")
class JSONSpring(
    @Autowired(required = false) jsonConfig: JSONConfig?,
    @Autowired(required = false) jsonLogFactory: LoggerFactory<*>?,
    @Autowired(required = false) jsonLogName: String?,
    @Autowired(required = false) jsonLogLevel: Level?,
    @Autowired(required = false) val jsonLogExclude: Collection<String>?,
) : AbstractJsonHttpMessageConverter() {

    private val config: JSONConfig = jsonConfig ?: JSONConfig.defaultConfig
    private val log: Logger? = jsonLogFactory?.let { factory ->
        jsonLogName?.let { factory.getLogger(it) } ?: factory.logger
    }
    private val level: Level = jsonLogLevel ?: Level.DEBUG

    override fun readInternal(resolvedType: Type, reader: Reader): Any {
        val json = JSONStreamer.parse(reader.buffered(), config.parseOptions)
        log?.log(level) { "JSON Input: ${json.elidedValue(exclude = jsonLogExclude)}" }
        return json?.fromJSONValue(resolvedType, config) ?: throw JSONException("Message may not be \"null\"")
    }

    override fun writeInternal(o: Any, type: Type?, writer: Writer) {
        log.let {
            if (it != null && it.isEnabled(level)) {
                val json = JSONSerializer.serialize(o, config)
                it.log(level) { "JSON Output: ${json.elidedValue(exclude = jsonLogExclude)}" }
                writer.appendJSONValue(json)
            }
            else
                writer.appendJSON(o, config)
        }
    }

}
