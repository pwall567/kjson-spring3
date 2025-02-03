/*
 * @(#) SpringTest.kt
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

package io.kjson.spring.test

import kotlin.test.Test

import java.time.LocalDate
import java.util.UUID

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.mock.http.client.MockClientHttpResponse
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.client.getForObject
import org.springframework.web.client.postForObject

import io.kstuff.test.shouldBe

import io.jstuff.log.LogList
import io.kstuff.log.isDebug
import io.kstuff.log.isError

import io.kjson.parseJSON
import io.kjson.spring.JSONSpring
import io.kjson.stringifyJSON

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [SpringTestConfiguration::class])
@AutoConfigureMockMvc
class SpringTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var restTemplateBuilder: RestTemplateBuilder

    @Test fun `should use kjson for output`() {
        mockMvc.get("/testendpoint") {
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content {
                string(responseString)
            }
        }
    }

    @Test fun `should use kjson for input`() {
        LogList().use { logList ->
            val expectedOutput = """{"DATE":"2022-07-04","extra":"0e457a9e-fb40-11ec-84d9-a324b304f4f9"}"""
            mockMvc.post("/testendpoint") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"ID":"0e457a9e-fb40-11ec-84d9-a324b304f4f9","name":"Me"}"""
                accept(MediaType.APPLICATION_JSON)
            }.andExpect {
                status { isOk() }
                content {
                    string(expectedOutput)
                }
            }
            logList.any {
                it.name == loggerName && it isDebug """JSON Input: {"ID":"****","name":"Me"}"""
            } shouldBe true
            logList.any {
                it.name == loggerName && it isDebug "JSON Output: $expectedOutput"
            } shouldBe true
        }
    }

    @Test fun `should log error on invalid JSON input`() {
        LogList().use { logList ->
            mockMvc.post("/testendpoint") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"ID":"12345","name":"Me"}"""
                accept(MediaType.APPLICATION_JSON)
            }.andExpect {
                status { isBadRequest() }
                content {
                    string("\"ERROR\"")
                }
            }
            logList.any {
                it.name == loggerName && it isError """JSON Input: {"ID":"****","name":"Me"}"""
            } shouldBe true
            logList.any {
                it.name == loggerName &&
                        it isError "Error deserializing io.kjson.spring.test.RequestData - Not a valid UUID - 12345"
            } shouldBe true
            logList.any {
                it.name == loggerName && it isDebug "JSON Output: \"ERROR\""
            } shouldBe true
        }
    }

    @Test fun `should use kjson for client response`() {
        LogList().use { logList ->
            val restTemplate = restTemplateBuilder.build()
            val mockRestServiceServer = MockRestServiceServer.createServer(restTemplate)
            mockRestServiceServer.expect(requestTo("/testclient")).andExpect(method(HttpMethod.GET)).andRespond {
                createResponse(responseString)
            }
            val response: ResponseData = restTemplate.getForObject("/testclient")
            response.date shouldBe LocalDate.of(2022, 7, 1)
            response.extra shouldBe "Hello!"
            mockRestServiceServer.verify()
            logList.any {
                it.name == loggerName && it isDebug """JSON Input: {"DATE":"2022-07-01","extra":"Hello!"}"""
            } shouldBe true
        }
    }

    @Test fun `should use kjson for client request`() {
        LogList().use { logList ->
            val restTemplate = restTemplateBuilder.build()
            val mockRestServiceServer = MockRestServiceServer.createServer(restTemplate)
            mockRestServiceServer.expect(requestTo("/testclient")).andExpect(method(HttpMethod.POST)).andRespond { r ->
                    val mockRequest = r as? MockClientHttpRequest ?: throw AssertionError("Not a MockClientHttpRequest")
                    // the following line will fail if the POST data was not serialised correctly (by kjson)
                    val data: RequestData =
                        mockRequest.bodyAsString.parseJSON() ?: throw AssertionError("Must not be null")
                    val response = ResponseData(LocalDate.of(2022, 7, 25), data.id.toString()).stringifyJSON()
                    createResponse(response)
                }
            val id = UUID.fromString("79c2e130-0bb7-11ed-99fa-e322ce878a96")
            val response: ResponseData = restTemplate.postForObject("/testclient", RequestData(id, "Anything"))
            response.date shouldBe LocalDate.of(2022, 7, 25)
            response.extra shouldBe id.toString()
            mockRestServiceServer.verify()
            logList.any {
                it.name == loggerName && it isDebug """JSON Output: {"ID":"****","name":"Anything"}"""
            } shouldBe true
        }
    }

    private fun createResponse(str: String) = MockClientHttpResponse(str.toByteArray(), HttpStatus.OK).apply {
        headers.contentType = MediaType.APPLICATION_JSON
    }

    companion object {
        val loggerName = JSONSpring::class.qualifiedName
        @Suppress("ConstPropertyName")
        const val responseString = """{"DATE":"2022-07-01","extra":"Hello!"}"""
    }

}
