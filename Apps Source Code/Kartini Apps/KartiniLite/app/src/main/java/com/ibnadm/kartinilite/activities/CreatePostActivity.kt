package com.ibnadm.kartinilite.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Post
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_report.*
import java.util.*
import kotlin.time.days

class CreatePostActivity : AppCompatActivity() {
    val TAG = "CreatePostActivity"

    private var mAuth: FirebaseAuth? = null
    private lateinit var dbRef: DatabaseReference

    private val PERMISSIONS_REQUIRED = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private val RC_PERMISSION = 101
    private val RC_SELECT_IMAGE = 102

    private var selectImgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        mAuth = FirebaseAuth.getInstance()

        btn_backpost.setOnClickListener {
            onBackPressed()
        }
        btn_import_a_img_post.setOnClickListener {
            pickImg()
        }
        btn_post.setOnClickListener {
            if (et_post.text.isEmpty()){
                Toast.makeText(this, "Harus di isi semua dengan lengkap!",Toast.LENGTH_SHORT).show()
            }else{
                if (selectImgUri != null){
                    performPostWithImg()
                }  else {
                    performPostWithoutImg()
                }
            }

        }
        askReqPermissions()
    }
    private fun performPostWithImg() {
        if (selectImgUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/posts/$filename")
        ref.putFile(selectImgUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Success Upload Images ! ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG,"File location: $it")
                    val fromUid = mAuth!!.currentUser!!.uid
                    val ref = FirebaseDatabase.getInstance().reference.child("posts").push()
                    val post = Post()
                    post.id = ref.key!!
                    post.fromUid = fromUid
                    post.text = et_post.text.toString().trim()
                    post.imgUrl = it.toString().trim()
                    post.timestamp = System.currentTimeMillis()
                    ref.setValue(post)
                    Toast.makeText(this,"Post Terkirim !", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ForumActivity::class.java)
                    startActivity(intent)
                }
            }
    }
    private fun performPostWithoutImg(){
        val fromUid = mAuth!!.currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference.child("posts").push()
        val post = Post()
        post.id = ref.key!!
        post.fromUid = fromUid
        post.text = et_post.text.toString().trim()
        post.timestamp = System.currentTimeMillis()
        ref.setValue(post)
        Toast.makeText(this,"Post Terkirim !", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ForumActivity::class.java)
        startActivity(intent)
    }
    private fun pickImg() {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent,RC_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "photo was selected")
            selectImgUri = data.data
            btn_import_a_img_post.text = selectImgUri.toString()
        }
    }
    private fun arePermissionGranted() : Boolean {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun askReqPermissions() {
        if (!arePermissionGranted()) {
            ActivityCompat.requestPermissions(this,PERMISSIONS_REQUIRED, RC_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            RC_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,"PermissionNot Granted", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
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