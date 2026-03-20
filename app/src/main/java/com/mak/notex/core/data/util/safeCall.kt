package com.mak.notex.core.data.util

import com.google.gson.JsonSyntaxException
import com.mak.notex.data.remote.dto.ApiResponse
import com.mak.notex.utils.mapHttpError
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException


suspend inline fun <T> safeCall(
    crossinline execute: suspend () -> Response<ApiResponse<T>>
): Result<T, NetworkError> {
    return try {
        val response = execute()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                if (body.data != null) {
                    Result.Success(body.data)
                } else {
                    Result.Error(NetworkError.EMPTY_HAND)
                }
            } else {
                Result.Error(NetworkError.EMPTY_BODY)
            }
        } else {
            Result.Error(mapHttpError(response.code()))
        }

    } catch (e: IOException) {
        Result.Error(NetworkError.NO_INTERNET)

    } catch (e: HttpException) {
        Result.Error(mapHttpError(e.code()))

    } catch (e: JsonSyntaxException) {     // Gson
        Result.Error(NetworkError.SERIALIZATION)

    } catch (e: CancellationException) {
        throw e

    } catch (e: Exception) {
        Result.Error(NetworkError.UNKNOWN)
    }
}

