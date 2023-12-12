package com.kelompokNizarBersaudara.krambilsawit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.ktx.Firebase
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
    private lateinit var signInInputsArray: Array<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tvSignUp: TextView = findViewById(R.id.tv_signUp)
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
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

        submitLogin.setOnClickListener {
            onSignIn()
        }
    }

    private fun onSignIn() {
        signInEmail = signInInputsArray[0].text.toString().trim()
        signInPassword = signInInputsArray[1].text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        goToMainActivity()
                        toast("signed in successfully")
                        finish()
                    } else {
                        toast("sign in failed")
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
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