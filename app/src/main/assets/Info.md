# 🌐 Modern Retrofit Setup with Kotlinx Serialization

A production-ready Android networking setup using **Retrofit**, **Kotlinx Serialization**, and **Coroutines**.  
This approach is Kotlin-first, safe, clean, and scalable.

---

## ✨ Why Kotlinx Serialization?

Kotlinx Serialization is the modern alternative to Gson, built specifically for Kotlin.

**Key benefits**
- ✅ Kotlin-native & JetBrains supported
- ⚡ Compile-time safety (no reflection)
- 🧼 Cleaner data models
- 🤝 Seamless coroutine integration

---

## 7️⃣ Use Kotlinx Serialization (Modern Alternative)

### 📦 Gradle Setup

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}
```

> ⚠️ Make sure the **Kotlin Serialization plugin** is enabled in your project.

---

### 🧩 Data Model

```kotlin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    @SerialName("email_address")
    val emailAddress: String
)
```

---

## 8️⃣ Complete Modern Retrofit Setup

### 🔧 Retrofit Configuration

```kotlin
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.example.com/")
    .addConverterFactory(
        json.asConverterFactory("application/json".toMediaType())
    )
    .build()
```

---

### 🌐 API Service

```kotlin
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: Long
    ): Response<UserDto>

    @POST("users")
    suspend fun createUser(
        @Body user: CreateUserRequest
    ): Response<UserDto>
}
```

---

### 🗂 Repository Pattern

```kotlin
class UserRepository(
    private val apiService: ApiService
) {

    suspend fun getUser(id: Long): Result<User> {
        return try {
            val response = apiService.getUser(id)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.toDomain())
            } else {
                Result.Error(Exception("HTTP ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

---

## ✅ Final Result

- 🚀 Kotlin-first serialization
- 🧼 Clean architecture
- 🔐 Safer JSON parsing
- ⚡ Coroutine-friendly networking
- 🏗 Production-ready setup

---

Happy coding! 🎯
