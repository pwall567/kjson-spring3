/*
 * @(#) JSONSpring.kt
 *
 * kjson-spring3  Spring Boot 3 JSON message converter for kjson
 * Copyright (c) 2022, 2023, 2024 Peter Wall
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

import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter

import io.jstuff.log.Level
import io.jstuff.log.Logger
import io.jstuff.log.LoggerFactory

import io.kjson.JSON.appendJSONValue
import io.kjson.JSON.elidedValue
import io.kjson.JSON.toJSON
import io.kjson.JSONConfig
import io.kjson.JSONException
import io.kjson.JSONSerializer
import io.kjson.JSONStringify.appendJSON
import io.kjson.JSONValue
import io.kjson.fromJSONValue
import io.kjson.parser.Parser

/**
 * Spring message converter to convert messages to and from JSON using the [kjson](https://github.com/pwall567/kjson)
 * library.  This class is expected to be instantiated by the [JSONSpringAutoConfiguration] class, but it may be
 * configured separately if required.
 *
 * @author  Peter Wall
 */
open class JSONSpring(
    jsonConfig: JSONConfig?,
    jsonLogFactory: LoggerFactory<*>?,
    jsonLogName: String?,
    jsonLogLevel: Level?,
    private val jsonLogExclude: Collection<String>?,
) : AbstractJsonHttpMessageConverter() {

    private val config: JSONConfig = jsonConfig ?: JSONConfig.defaultConfig
    private val log: Logger? = jsonLogFactory?.let { factory ->
        jsonLogName?.let { factory.getLogger(it) } ?: factory.logger
    }
    private val level: Level = jsonLogLevel ?: Level.DEBUG

    override fun readInternal(resolvedType: Type, reader: Reader): Any {
        val json = Parser.parse(reader.readText(), config.parseOptions) ?:
                throw JSONException("JSON may not be \"null\"")
        val result = try {
            json.fromJSONValue(resolvedType, config)
        } catch (je: JSONException) {
            log?.let {
                it.error { "JSON Input: ${json.displayValue()}" }
                it.error { je.message }
            }
            throw je
        }
        log?.log(level) { "JSON Input: ${json.displayValue()}" }
        return result ?: throw JSONException("Deserialized JSON may not be null")
    }

    override fun writeInternal(o: Any, type: Type?, writer: Writer) {
        log.let {
            if (it != null && it.isEnabled(level)) {
                val json = JSONSerializer.serialize(o, config)
                it.log(level) { "JSON Output: ${json.displayValue()}" }
                writer.appendJSONValue(json)
            }
            else
                writer.appendJSON(o, config)
        }
    }

    private fun JSONValue?.displayValue(): String = if (jsonLogExclude != null)
        elidedValue(exclude = jsonLogExclude)
    else
        toJSON()

}
