package network

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import models.ApiResponse
import models.ChatMessage
import models.ModelConfig
import models.ModelProvider

object ApiService {
    suspend fun HttpClient.getProducts(page: Int = 0) =
        getResults<ApiResponse> {
            url("https://dummyjson.com/products?limit=10&skip=${page * 10}")
            method = HttpMethod.Get
        }

}