package com.ibnadm.kartinilite.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.User
import com.ibnadm.kartinilite.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : BaseActivity(), View.OnClickListener {
    val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth


    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mQuery: Query
    private lateinit var messagesQueryListener: ChildEventListener

    val RC_SIGN_IN_GOOGLE = 9966

    private var profileListener: ValueEventListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = Firebase.database.reference

        facebookLogin()
        googleLogin()

    }

    private fun facebookLogin() {
        callbackManager = CallbackManager.Factory.create()

        btn_facebook_sign_in.setReadPermissions("email", "public_profile")
        btn_facebook_sign_in.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    Log.d(TAG, "facebook:onSuccess:$result")
                    handleFacebookAccesToken(result!!.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                    updateUI(null)
                }

                override fun onError(error: FacebookException?) {
                    Log.d(TAG, "facebook:onError", error)
                    updateUI(null)
                }
            })
    }

    private fun googleLogin() {
        btn_google_sign_in.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException){
                Log.w(TAG,"Google Sign In Failed.")
                updateUI(null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id)
        showProgressDialog()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG,"SignInWithCredential:success")
                    val currentUser = mAuth.currentUser
                    Log.d(TAG, "signInWithCredential:succes")
                    if (task.result!!.additionalUserInfo!!.isNewUser){
                        checkData()
                    }
                    updateUI(currentUser)
                } else {
                    Log.d(TAG,"SignInWithCredential:failure")
                    updateUI(null)
                }
                hideProgressDialog()
            }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE)
    }

    private fun handleFacebookAccesToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        showProgressDialog()

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = mAuth.currentUser
                    Log.d(TAG, "signInWithCredential:succes")
                    if (task.result!!.additionalUserInfo!!.isNewUser){
                        checkData()
                    }
                    updateUI(currentUser)
                } else {
                    Log.w(TAG, "signInWithCredential:failure")
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
                hideProgressDialog()
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) startActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }

    override fun onClick(v: View?) {
        val i = v!!.id
        when(i) {
            R.id.btn_google_sign_in -> signIn()
        }
    }
    private fun storeUser(user: User) {
        mDatabase.child("users").child(user.uid).setValue(user)
            .addOnSuccessListener {
                Log.d(TAG,"Store user with uid:${user.uid}")
            }
    }

    private fun checkData() {
        val currentUser = mAuth.currentUser
        val user = User()
        user.uid = currentUser!!.uid
        user.fullName = currentUser.displayName.toString()
        user.email = currentUser.email.toString()
        user.photoUrl = currentUser.photoUrl.toString()
        user.phoneNumber = currentUser.phoneNumber.toString()
        user.sex = "Female"
        storeUser(user)
        updateUI(currentUser)
    }

}
