import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.HttpException
import android.util.Log
import com.google.gson.annotations.SerializedName

data class PostRequest(
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("login_register_code") val loginRegisterCode: Int,
    @SerializedName("uuid_request") val uuidRequest: String
)

interface ApiService {
    @POST("API_SENPULSE_AQUI")
    suspend fun postRequest(@Body request: PostRequest): Response<Unit>
}

object RetrofitInstance {
    private const val BASE_URL = "https://events.sendpulse.com/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

suspend fun postRequest(phone: String, loginRegisterCode: Int, uuidRequest: String) {
    try {
        val response = RetrofitInstance.api.postRequest(
            PostRequest(
                email = "",
                phone = phone,
                loginRegisterCode = loginRegisterCode,
                uuidRequest = uuidRequest
            )
        )

        if (response.isSuccessful) {
            Log.i("sms", "Response status: ${response.code()}")
        } else {
            Log.i("sms", "Error response code: ${response.code()}")
        }
    } catch (e: HttpException) {
        Log.i("sms", "HTTP error: ${e.message()}")
    } catch (e: Exception) {
        Log.i("sms", "Error: ${e.message}")
    }
}
