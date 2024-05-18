package tn.esprit.androidapplicationtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var chatSdk: ChatSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sam = User(5, "Sam", R.drawable.sam)
        val steven = User(7, "Steven", R.drawable.steven)
        val olivia = User(4, "Alice", R.drawable.olivia)
        val john = User(3, "John", R.drawable.john)
        val greg = User(1, "Greg", R.drawable.greg)

        val users: List<User> = listOf(sam, steven, olivia, john, greg)
        val apiKey = "80e01067bd82ddb68ce5091bd07c8e1946ef4626"
        val currentUser = "Alice"
        // Optional: Define custom styles
        val chatStyle = ChatStyle(
            textColor = getColor(R.color.red)
        )
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val recyclerViewUser: RecyclerView = findViewById(R.id.userIconRecyclerView)

        chatSdk = ChatSdk(this, currentUser, apiKey, users)

        chatSdk.initializeViews(recyclerView, recyclerViewUser)
    }

    override fun onResume() {
        super.onResume()
        chatSdk.fetchAndDisplayConversations()
    }
}
