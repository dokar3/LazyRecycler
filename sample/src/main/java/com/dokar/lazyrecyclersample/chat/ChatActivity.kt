package com.dokar.lazyrecyclersample.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.LazyRecycler
import com.dokar.lazyrecycler.flow.asMutSource
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.template
import com.dokar.lazyrecyclersample.ChatItem
import com.dokar.lazyrecyclersample.Constants.CHAT_MESSAGES
import com.dokar.lazyrecyclersample.Message
import com.dokar.lazyrecyclersample.SentDate
import com.dokar.lazyrecyclersample.databinding.ActivityChatBinding
import com.dokar.lazyrecyclersample.databinding.ItemMsgDateBinding
import com.dokar.lazyrecyclersample.databinding.ItemMsgFriendBinding
import com.dokar.lazyrecyclersample.databinding.ItemMsgMeBinding
import com.dokar.lazyrecyclersample.isSameMinute
import com.dokar.lazyrecyclersample.isToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private val messages: MutableStateFlow<List<ChatItem>> = MutableStateFlow(emptyList())

    private val earlierFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    private val todayFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private var prevSentDate: SentDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createList(binding.rvChat)

        groupMessages(CHAT_MESSAGES)

        binding.cardSend.setOnClickListener {
            val text = binding.etInput.text
            if (text.isNullOrEmpty()) {
                return@setOnClickListener
            }
            newMessage(text.toString())
        }
    }

    private fun createList(rv: RecyclerView) {
        LazyRecycler(rv) {
            val sentDate = template { binding: ItemMsgDateBinding, item: ChatItem ->
                val sentDate = item as SentDate
                binding.tvSentDate.text = sentDate.dateText
            }
            val fromFriend = template { binding: ItemMsgFriendBinding, item: ChatItem ->
                val msg = item as Message
                binding.tvUsername.text = msg.senderName
                binding.tvMessage.text = msg.content
                binding.ivAvatar.setImageResource(msg.senderAvatar)
            }
            val fromMe = template { binding: ItemMsgMeBinding, item: ChatItem ->
                val msg = item as Message
                binding.tvUsername.text = msg.senderName
                binding.tvMessage.text = msg.content
                binding.ivAvatar.setImageResource(msg.senderAvatar)
            }

            items(
                fromFriend,
                messages.asMutSource(lifecycleScope)
            ).subSection(fromMe) { item, _ ->
                item is Message && item.senderId == 0
            }.subSection(sentDate) { item, _ ->
                item is SentDate
            }.differ {
                areItemsTheSame { oldItem, newItem ->
                    if (oldItem::class != newItem::class) {
                        false
                    } else {
                        oldItem.id == newItem.id
                    }
                }
                areContentsTheSame { oldItem, newItem ->
                    if (oldItem is Message && newItem is Message) {
                        oldItem.content == newItem.content
                    } else if (oldItem is SentDate && newItem is SentDate) {
                        oldItem.dateText == newItem.dateText
                    } else {
                        false
                    }
                }
            }
        }
    }

    private fun groupMessages(list: List<Message>) = lifecycleScope.launch(Dispatchers.Default) {
        if (list.isEmpty()) {
            messages.value = emptyList()
            return@launch
        }
        var dateId = 0
        prevSentDate = newSentDate(dateId++, list[0].sentAt)
        val groupedMsgs = CHAT_MESSAGES.groupBy { msg ->
            if (msg.sentAt.isSameMinute(prevSentDate!!.date)) {
                prevSentDate!!
            } else {
                newSentDate(dateId++, msg.sentAt).also {
                    prevSentDate = it
                }
            }
        }
        val chatMessages = mutableListOf<ChatItem>()
        for ((sentDate, msgs) in groupedMsgs) {
            chatMessages.add(sentDate)
            chatMessages.addAll(msgs)
        }

        withContext(Dispatchers.Main) {
            messages.value = chatMessages
            binding.rvChat.let {
                it.post {
                    it.scrollToPosition(chatMessages.size - 1)
                }
            }
        }
    }

    private fun newMessage(content: String) {
        val msgs = messages.value.toMutableList()
        val me = msgs.find { it is Message && it.senderId == 0 } as Message
        val newMsg = Message(
            me.senderId,
            me.senderName,
            me.senderAvatar,
            content,
            System.currentTimeMillis()
        )
        val prevDate = prevSentDate
        if (prevDate == null || !prevDate.date.isSameMinute(newMsg.sentAt)) {
            val id = if (prevDate != null) prevDate.id + 1 else 0
            newSentDate(id, newMsg.sentAt).also {
                prevSentDate = it
                msgs.add(it)
            }
        }
        msgs.add(newMsg)
        messages.value = msgs

        randomlyReply()

        binding.etInput.setText("")
        binding.rvChat.let {
            it.post {
                it.smoothScrollToPosition(messages.value.size - 1)
            }
        }
    }

    private fun newSentDate(id: Int, sentAt: Long): SentDate {
        val dateText = if (sentAt.isToday()) {
            todayFormat.format(sentAt)
        } else {
            earlierFormat.format(sentAt)
        }
        return SentDate(id, sentAt, dateText)
    }

    private fun randomlyReply() {
        if (Random.nextFloat() < 0.3f) {
            return
        }
        val msgs = messages.value.toMutableList()
        val friend = msgs.find { it is Message && it.senderId != 0 } as Message
        val latest = msgs.last() as Message
        val reply = Message(
            friend.senderId,
            friend.senderName,
            friend.senderAvatar,
            "${latest.content}?",
            System.currentTimeMillis()
        )
        msgs.add(reply)
        messages.value = msgs
    }
}
