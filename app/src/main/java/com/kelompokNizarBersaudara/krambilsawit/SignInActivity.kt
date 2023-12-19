package com.kelompokNizarBersaudara.krambilsawit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.GoogleAuthProvider
import com.kelompokNizarBersaudara.krambilsawit.databinding.ActivitySignInBinding
import com.kelompokNizarBersaudara.krambilsawit.extensions.Extensions.toast
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseAuth
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseUser

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInEmail: String
    private lateinit var signInPassword: String

    /**
     * 1 user_email
     * 2 user_password
     */
    private lateinit var signInInputsArray: Array<TextInputEditText>

    // Google Sign In
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::signInGoogleFirebaseContract)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val tvSignUp: TextView = findViewById(R.id.tv_signUp)
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
            return@setOnClickListener
        }
    }

    public override fun onStart() {
        super.onStart()

        if (firebaseUser == null) {
            signIn()
        } else {
            goToMainActivity()
        }
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun signIn() {
        signInInputsArray = arrayOf(findViewById(R.id.user_email), findViewById(R.id.user_password))
        val submitLogin: Button = findViewById(R.id.tv_login)
        val googleLogin: Button = findViewById(R.id.tv_google)

        googleLogin.setOnClickListener {
            onSignInGoogle()
        }

        submitLogin.setOnClickListener {
            onSignIn()
        }
    }

    private fun onSignInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun signInGoogleFirebaseContract(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                toast("sign in failed")
            }
        }  else {
            toast("There was an error signing in")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    toast("signed in successfully")
                    goToMainActivity()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    toast("sign in failed")
                }
            }
    }

    private fun onSignIn() {
        signInEmail = signInInputsArray[0].text.toString().trim()
        signInPassword = signInInputsArray[1].text.toString().trim()

        if (notEmpty()) {
            signInEmailAndPassword(signInEmail, signInPassword)
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }

    private fun signInEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    toast("signed in successfully")
                    goToMainActivity()
                } else {
                    toast("sign in failed")
                    Log.w(TAG, "signInWithEmailAndPassword:failure")
                }
            }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}