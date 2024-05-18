package tn.esprit.androidapplicationtest

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Conversation(
    val _id: String,
    val participants: List<String>,
    val messages: List<Message>
)

data class Message(
    val _id: String,
    val sender: Sender,
    val content: String
)

data class User(
    val id: Int,
    val name: String,
    val imageResourceId: Int // Resource ID for the user's image
)

data class Sender(
    val name: String
)
data class ChatStyle(
    val backgroundColor: Int? = null,
    val textColor: Int? = null,
    val senderNameTextSize: Float? = null,
    val messageContentTextSize: Float? = null,
    val senderNameTextStyle: Int? = null,
    val messageContentTextStyle: Int? = null,
    val itemPadding: Int? = null,
    val itemMarginLeft: Int? = null,
    val itemMarginTop: Int? = null,
    val itemMarginRight: Int? = null,
    val itemMarginBottom: Int? = null,
    val photoBorderColor: Int? = null,
    val photoBorderWidth: Int? = null,
    // Add more properties as needed
)


interface ConversationService {
    @GET("conversation/{currentUser}")
    fun getConversations(@retrofit2.http.Path("currentUser") currentUser: String): Call<List<Conversation>>
}

class ChatSdk(
    private val context: Context,
    private val currentUser: String,
    private val apiKey: String,
    private val users: List<User>,
    private val chatStyle: ChatStyle? = null // Optional style parameter

) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewUser: RecyclerView
    private lateinit var adapter: ContactAdapter
    private lateinit var adapterUser: UserAdapter
    private lateinit var service: ConversationService

    init {
        initializeRetrofit()
    }

    private fun initializeRetrofit() {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-secret-key", apiKey)
                .build()
            chain.proceed(request)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/") // Ensure the URL ends with "/"
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(ConversationService::class.java)
    }

    fun initializeViews(
        recyclerView: RecyclerView,
        recyclerViewUser: RecyclerView
    ) {
        this.recyclerView = recyclerView
        this.recyclerViewUser = recyclerViewUser

        displayUsers(users)
    }

    fun fetchAndDisplayConversations() {
        service.getConversations(currentUser).enqueue(object : Callback<List<Conversation>> {
            override fun onResponse(
                call: Call<List<Conversation>>,
                response: Response<List<Conversation>>
            ) {
                if (response.isSuccessful) {
                    val conversations = response.body()
                    conversations?.let {
                        Log.d("ChatSdk", "Number of conversations received: ${it.size}")
                        displayConversations(it)
                    }
                } else {
                    Log.e("ChatSdk", "Failed to fetch data: ${response.code()}")
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Conversation>>, t: Throwable) {
                Log.e("ChatSdk", "Network Error: ${t.message}")
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayConversations(conversations: List<Conversation>) {
        adapter = ContactAdapter(context, conversations, users, currentUser, chatStyle)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun displayUsers(users: List<User>) {
        adapterUser = UserAdapter(context, users)
        recyclerViewUser.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUser.adapter = adapterUser
    }
}
