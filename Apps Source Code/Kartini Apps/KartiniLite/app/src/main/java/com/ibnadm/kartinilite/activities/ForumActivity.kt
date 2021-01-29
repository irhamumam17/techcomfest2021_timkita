package com.ibnadm.kartinilite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Post
import com.ibnadm.kartinilite.models.User
import com.ibnadm.kartinilite.utils.PostItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_forum.*

class ForumActivity : AppCompatActivity() {
    val TAG = "ChatLogActivity"
    companion object {
        val POST_KEY = "POST_KEY"
    }
    private var mAuth: FirebaseAuth? = null
    private var toast: Toast? = null

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)
        mAuth = FirebaseAuth.getInstance()
        toast = Toast.makeText(
            this,
            "Silahkan Lengkapi profil anda untuk menggunakan feature Kartini !",
            Toast.LENGTH_SHORT
        )

        rv_post.adapter = adapter

        val cUid = mAuth!!.currentUser!!.uid
        val refCek = FirebaseDatabase.getInstance().reference.child("users/$cUid")
        refCek.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val profile = p0.getValue(User::class.java) ?: return
                adapter.setOnItemClickListener { item, view ->
                    Log.d(TAG, "Try to click adapter")
                    if (profile.nik.isEmpty() || profile.nik == "-") {
                        toast!!.show()
                    } else {
                        val postItem = item as PostItem
                        val intent = Intent(this@ForumActivity, DetailPostActivity::class.java)
                        intent.putExtra(POST_KEY, postItem.post)
                        startActivity(intent)
                        finish()
                    }
                }
                fab_post.setOnClickListener {
                    if (profile.nik.isEmpty() || profile.nik == "-") {
                        toast!!.show()
                    } else {
                        val intent = Intent(this@ForumActivity, CreatePostActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

        })
        btn_backforum.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        listenPost()
    }
    private fun listenPost() {
        val ref = FirebaseDatabase.getInstance().reference.child("posts")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG,p0.toException().toString())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val post = p0.getValue(Post::class.java) ?: return
                Log.d(TAG, "try to fetch postid = $post.id")
                adapter.add(PostItem(post))
                rv_post.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val post = p0.getValue(Post::class.java) ?: return
                Log.d(TAG, "try to fetch postid = $post.id")
                adapter.add(PostItem(post))
                rv_post.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
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