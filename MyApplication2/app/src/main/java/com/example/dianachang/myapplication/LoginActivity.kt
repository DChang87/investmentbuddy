package com.example.dianachang.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.widget.ImageButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential






class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    val mAuth = FirebaseAuth.getInstance();

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val i = Intent(this, MainActivity::class.java) // Your list's Intent
            i.flags = Intent.FLAG_ACTIVITY_NO_HISTORY; // Adds the FLAG_ACTIVITY_NO_HISTORY flag
            startActivity(i);
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("532488949079-esqpji5s7c0eh6m2h1f6hi10i24ebf2o.apps.googleusercontent.com")
                .requestEmail()
                .build()
        val mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        findViewById<ImageButton>(R.id.sign_in_button).setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, 1)
        };

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Signed in successfully, show authenticated UI.
                val acct = result.signInAccount!!
                val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = mAuth.currentUser
                                val i = Intent(this, MainActivity::class.java) // Your list's Intent
                                i.flags = Intent.FLAG_ACTIVITY_NO_HISTORY; // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                                startActivity(i);
                            } else {
                                Toast.makeText(this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }

            } else {
            }
        }
    }
}
