package com.ibnadm.kartinilite.activities

import android.accounts.AccountManager.get
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.User
import com.ibnadm.kartinilite.utils.UserItem
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    val TAG = "NewMessageActivity"

    //Firebase Auth Object.
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        mAuth = FirebaseAuth.getInstance()
        btn_back_selectuser.setOnClickListener{
            onBackPressed()
        }
        fetchUser()
    }
    companion object {
        val USER_KEY = "USER_KEY"
    }
    private fun fetchUser() {
        val ref = FirebaseDatabase.getInstance().reference.child("users").endAt(mAuth!!.currentUser!!.uid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                for (child in p0.children) {
                    Log.d(TAG,child.toString())
                    val user = child.getValue(User::class.java)
                    if (user != null){
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener{ item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }
                rv_new_message.adapter = adapter
            }

        })
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