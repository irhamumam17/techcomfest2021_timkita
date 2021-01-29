package com.ibnadm.kartinilite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Message
import com.ibnadm.kartinilite.models.User
import com.ibnadm.kartinilite.utils.ChatFromItem
import com.ibnadm.kartinilite.utils.ChatToItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {
    val TAG = "ChatLogActivity"
    private var mAuth: FirebaseAuth? = null

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        mAuth = FirebaseAuth.getInstance()
        rv_chatitems.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        tv_name_onchat.text = toUser!!.fullName
        btn_back_selectuser2.setOnClickListener{
            onBackPressed()
        }
        btn_send_msg.setOnClickListener{
            Log.d(TAG, "Send a message")
            performeSendMessage()
        }
        listenForMessage()
    }
    private fun listenForMessage() {
        val fromUid = mAuth!!.currentUser!!.uid
        val toUid = toUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromUid/$toUid")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java)

                if (message != null) {
                    Log.d(TAG, message.text)
                    if (message.fromUid == mAuth!!.currentUser!!.uid) {
                        val currentUser = LatestMessagesActivity.currentUser?: return
                        adapter.add(ChatToItem(message,currentUser))
                    } else {
                        adapter.add(ChatFromItem(message, toUser!!))
                    }

                }

                rv_chatitems.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun performeSendMessage() {
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val fromUid = mAuth!!.currentUser!!.uid
        val toUid = user.uid
        val ref = FirebaseDatabase.getInstance().reference.child("user-messages/$fromUid/$toUid").push()
        val toRef = FirebaseDatabase.getInstance().reference.child("user-messages/$toUid/$fromUid").push()

        if (mAuth!!.currentUser == null) return
        val message = Message()
        message.id = ref.key!!
        message.text = et_enter_msg.text.toString()
        message.fromUid = fromUid
        message.toUid = toUid
        message.timestamp = (System.currentTimeMillis())
        ref.setValue(message).addOnSuccessListener {
            Log.d(TAG, "Saved our chat message : ${ref.key}")
            et_enter_msg.text.clear()
            rv_chatitems.scrollToPosition(adapter.itemCount - 1)
        }
        toRef.setValue(message)
        val latestMsgRef = FirebaseDatabase.getInstance().reference.child("latest-messages/$fromUid/$toUid")
        val latestMsgToRef = FirebaseDatabase.getInstance().reference.child("latest-messages/$toUid/$fromUid")
        latestMsgRef.setValue(message)
        latestMsgToRef.setValue(message)
    }

    override fun onStart() {
        super.onStart()
        updateUI(mAuth!!.currentUser)
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}