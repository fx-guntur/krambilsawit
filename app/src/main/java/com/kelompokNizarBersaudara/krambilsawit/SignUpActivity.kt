package com.kelompokNizarBersaudara.krambilsawit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.UserProfileChangeRequest
import com.kelompokNizarBersaudara.krambilsawit.databinding.ActivitySignUpBinding
import com.kelompokNizarBersaudara.krambilsawit.extensions.Extensions.toast
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseUser
import com.kelompokNizarBersaudara.krambilsawit.utils.FirebaseUtils.firebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var signUpName: String
    private lateinit var signUpEmail: String
    private lateinit var signUpPassword: String

    /**
     * 1 user_name
     * 2 user_email
     * 3 user_password
     * 4 user_password_confirm
     */
    private lateinit var signUpInputsArray: Array<TextInputEditText>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signUpInputsArray = arrayOf(
            findViewById(R.id.user_name),
            findViewById(R.id.user_email),
            findViewById(R.id.user_password),
            findViewById(R.id.user_password_confirm))

        val tvBack: Button = findViewById(R.id.tv_back)
        tvBack.setOnClickListener {
            goToSignInActivity()
        }
    }

    public override fun onStart() {
        super.onStart()

        firebaseUser?.reload()
        if (firebaseUser == null) {
            signUp()
        } else {
            goToSignInActivity()
        }
    }

    private fun notEmpty(): Boolean = signUpInputsArray[0].text.toString().trim().isNotEmpty() &&
            signUpInputsArray[1].text.toString().trim().isNotEmpty() &&
            signUpInputsArray[2].text.toString().trim().isNotEmpty() &&
            signUpInputsArray[3].text.toString().trim().isNotEmpty()

    private fun signUp() {
        val submitRegister: Button = findViewById(R.id.tv_updateuserdetail)
        submitRegister.setOnClickListener {
            onSignUp()
        }
    }

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            signUpInputsArray[2].text.toString().trim() == signUpInputsArray[3].text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            signUpInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        } else {
            toast("passwords are not matching !")
        }
        return identical
    }

    private fun onSignUp() {
        if (identicalPassword()) {
            signUpName = signUpInputsArray[0].text.toString().trim()
            signUpEmail = signUpInputsArray[1].text.toString().trim()
            signUpPassword = signUpInputsArray[2].text.toString().trim()
            println(signUpEmail)

            firebaseAuth.createUserWithEmailAndPassword(signUpEmail, signUpPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signInEmailAndPassword(signUpEmail, signUpPassword)
                        toast("created account successfully !")
                    } else {
                        toast("failed to Authenticate !")
                    }
                }
        }
    }

    private fun signInEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    val user = firebaseUser
                    user?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(signUpName)
                            .build())
                    goToMainActivity()
                } else {
                    toast("sign in failed")
                    Log.w(SignUpActivity.TAG, "signInWithEmailAndPassword:failure")
                    goToSignInActivity()
                }
            }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun goToSignInActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "SignUpActivity"
    }
}