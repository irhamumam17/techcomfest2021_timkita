package com.ibnadm.kartinilite.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Report
import kotlinx.android.synthetic.main.activity_report.*
import java.util.*

class ReportActivity : AppCompatActivity() {
    val TAG = "ReportActivity"
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private val RC_PERMISSION = 101
    private val RC_SELECT_IMAGE = 102

    private var mAuth:FirebaseAuth? = null
    private lateinit var dbRef: DatabaseReference

    private var selectImgUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        mAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference.child("reports")


        btn_backreport.setOnClickListener {
            onBackPressed()
        }
        btn_import_a_img.setOnClickListener {
            Log.d(TAG, "try to show img selector")
            pickImg()

        }
        askReqPermissions()
        btn_send_report.setOnClickListener {
            var sign = false
            if (et_title_report.text.toString().isEmpty() || et_text_report.text.toString().isEmpty() || btn_import_a_img.text.toString() == "Import a image") {
                Toast.makeText(this, "Harus di isi semua dengan lengkap!",Toast.LENGTH_SHORT).show()
            } else {
                sign = true
            }
            if (sign) {
                sendReport()
            }
        }
    }
    private fun pickImg() {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent,RC_SELECT_IMAGE)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "photo was selected")
            selectImgUri = data.data
            btn_import_a_img.text = selectImgUri.toString()
        }
    }
    private fun sendReport() {
        uploadImgToFirebase()
    }
    private fun uploadImgToFirebase() {
        if (selectImgUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/reports/$filename")
        ref.putFile(selectImgUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Success Upload Images ! ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG,"File location: $it")
                    val uid = mAuth!!.currentUser!!.uid
                    val report = Report()
                    report.id = dbRef.push().key.toString()
                    report.uid = uid
                    report.title = et_title_report.text.toString().trim()
                    report.text = et_text_report.text.toString().trim()
                    report.instance = 1
                    report.status = 1
                    report.verify = 1
                    report.proses = 1
                    report.timestamp = System.currentTimeMillis()
                    report.uri = it.toString().trim()
                    val idRef = FirebaseDatabase.getInstance().reference.child("reports-user/$uid/${report.id}")
                    dbRef.child(report.id).setValue(report).addOnCompleteListener {
                        idRef.setValue(report)
                        Toast.makeText(this,"Report Terkirim !", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LogReportActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        updateUI(mAuth!!.currentUser)
    }
}