package tn.esprit.androidapplicationtest

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tn.esprit.androidapplicationtest.databinding.ChatitemBinding


class ContactAdapter(private val context: Context, private val conversations: List<Conversation>, private val users: List<User>, private val currentUser: String,     private val chatStyle: ChatStyle? = null // Optional style parameter
) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ChatitemBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }



        fun bind(conversation: Conversation) {
            val lastMessage = conversation.messages.lastOrNull()
            val participants = conversation.participants
            val otherParticipantId = participants.firstOrNull { it != currentUser }
            val senderName = otherParticipantId ?: "Unknown"
            binding.senderName.text = senderName
            // Find the user with matching name and set its image
            val user = users.find { it.name == senderName }
            user?.let {
                binding.photo.setImageResource(it.imageResourceId)

            }
            if (lastMessage?.content!=null){
                if(lastMessage.content.length>20){
                    val truncatedMessage = lastMessage.content.substring(0, 20) + "..."
                    binding.messageContent.text = truncatedMessage
                }
                else binding.messageContent.text = lastMessage.content
            }else binding.messageContent.text =""
            // Apply styles if provided
            chatStyle?.let {
                it.backgroundColor?.let { color -> binding.root.setBackgroundColor(color) }
                it.textColor?.let { color ->
                    binding.senderName.setTextColor(color)
                    binding.messageContent.setTextColor(color)
                }
                it.senderNameTextSize?.let { size -> binding.senderName.textSize = size }
                it.messageContentTextSize?.let { size -> binding.messageContent.textSize = size }
                it.senderNameTextStyle?.let { style -> binding.senderName.setTypeface(null, style) }
                it.messageContentTextStyle?.let { style -> binding.messageContent.setTypeface(null, style) }
                it.itemPadding?.let { padding ->
                    binding.root.setPadding(padding, padding, padding, padding)
                }
                it.itemMarginLeft?.let { left ->
                    val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(
                        left,
                        it.itemMarginTop ?: 0,
                        it.itemMarginRight ?: 0,
                        it.itemMarginBottom ?: 0
                    )
                    binding.root.layoutParams = layoutParams
                }
            }
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val conversation = conversations[position]
                val conversationId = conversation._id
                var sendername = conversation.participants[1]
                val currentUserId = users.find { it.name == currentUser }?.id
                Log.d("currentuser", currentUserId.toString())

                val senderimage = users.find { it.name == sendername }?.imageResourceId
                Log.d("sender name adapter", sendername)
                val intent = Intent(context, MessengerActivity::class.java).apply {
                    putExtra("conversationId", conversationId)
                    putExtra("sendername",sendername)
                    putExtra("senderimage",senderimage)
                    putExtra("currentUserId",currentUserId.toString())

                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChatitemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int {
        return conversations.size
    }
}
