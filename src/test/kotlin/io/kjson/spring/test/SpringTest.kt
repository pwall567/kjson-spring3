/*
 * @(#) SpringTest.kt
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

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.expect

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

import io.kjson.parseJSON
import io.kjson.stringifyJSON
import net.pwall.log.LogList
import net.pwall.log.isDebug
import net.pwall.log.isError

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
            assertTrue(logList.any {
                it.name == "io.kjson.spring.JSONSpring" && it isDebug """JSON Input: {"ID":"****","name":"Me"}"""
            })
            assertTrue(logList.any {
                it.name == "io.kjson.spring.JSONSpring" && it isDebug "JSON Output: $expectedOutput"
            })
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
            assertTrue(logList.any {
                it.name == "io.kjson.spring.JSONSpring" && it isError """JSON Input: {"ID":"****","name":"Me"}"""
            })
            assertTrue(logList.any {
                it.name == "io.kjson.spring.JSONSpring" &&
                        it isError "Error deserializing \"12345\" as java.util.UUID at /ID"
            })
            assertTrue(logList.any {
                it.name == "io.kjson.spring.JSONSpring" && it isDebug "JSON Output: \"ERROR\""
            })
        }
    }

    @Test fun `should use kjson for client response`() {
        val restTemplate = restTemplateBuilder.build()
        val mockRestServiceServer = MockRestServiceServer.createServer(restTemplate)
        mockRestServiceServer.expect(requestTo("/testclient")).andExpect(method(HttpMethod.GET)).andRespond {
            createResponse(responseString)
        }
        val response: ResponseData = restTemplate.getForObject("/testclient")
        expect(LocalDate.of(2022, 7, 1)) { response.date }
        expect("Hello!") { response.extra }
        mockRestServiceServer.verify()
    }

    @Test fun `should use kjson for client request`() {
        val restTemplate = restTemplateBuilder.build()
        val mockRestServiceServer = MockRestServiceServer.createServer(restTemplate)
        mockRestServiceServer.expect(requestTo("/testclient")).andExpect(method(HttpMethod.POST)).andRespond { req ->
            val mockRequest = req as? MockClientHttpRequest ?: throw AssertionError("Not a MockClientHttpRequest")
            // the following line will fail if the POST data was not serialised correctly (by kjson)
            val data: RequestData = mockRequest.bodyAsString.parseJSON() ?: throw AssertionError("Must not be null")
            val response = ResponseData(LocalDate.of(2022, 7, 25), data.id.toString()).stringifyJSON()
            createResponse(response)
        }
        val id = UUID.fromString("79c2e130-0bb7-11ed-99fa-e322ce878a96")
        val response: ResponseData = restTemplate.postForObject("/testclient", RequestData(id, "Anything"))
        expect(LocalDate.of(2022, 7, 25)) { response.date }
        expect(id.toString()) { response.extra }
        mockRestServiceServer.verify()
    }

    private fun createResponse(str: String) = MockClientHttpResponse(str.toByteArray(), HttpStatus.OK).apply {
        headers.contentType = MediaType.APPLICATION_JSON
    }

    companion object {
        const val responseString = """{"DATE":"2022-07-01","extra":"Hello!"}"""
    }

}
