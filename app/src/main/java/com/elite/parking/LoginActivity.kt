package com.elite.parking

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.elite.parking.Model.login.LoginResponse
import com.elite.parking.Model.login.User
import com.elite.parking.databinding.ActivityLoginBinding
import com.elite.parking.storage.SharedPreferencesHelper
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private var userId: String = ""
    private var token: String = ""
    private var companyId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferencesHelper = SharedPreferencesHelper(this)

        supportActionBar?.title = "Login"

        progressDialog = ProgressDialog(this).apply {
            setTitle("Please wait")
            setMessage("Logging In...")
            setCanceledOnTouchOutside(false)
        }

        checkUser()

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        binding.loginButton.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailInput.error = "Invalid email format"
            }
            password.isEmpty() -> {
                binding.passwordInput.error = "Please enter password"
            }
            else -> {
                firebaseLogin(email, password)
            }
        }
    }

    private fun firebaseLogin(email: String, password: String) {
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()

                val firebaseUser = firebaseAuth.currentUser
                val userEmail = firebaseUser?.email ?: "Unknown"
                Toast.makeText(this, "Logged in as $userEmail", Toast.LENGTH_SHORT).show()

                // Example: Replace this part with your actual API call for loginResponse
                val dummyUser = User(
                    uuid = "123456",
                    name = "John Doe",
                    actionId = 1,
                    address = "New York",
                    mobileNumber = "9876543210",
                    companyId = "cmp001",
                    email = userEmail,
                    roleId = 2,
                    password = "password",
                    designation = "Developer",
                    status = 1,
                    createdDate = "2024-01-01",
                    createdBy = "Admin",
                    token = "abc123"
                )

                val loginResponse = LoginResponse(
                    mssg = "Login Successful",
                    content = "User logged in",
                    status = listOf(dummyUser)
                )

                // Save loginResponse into SharedPreferences
                sharedPreferencesHelper.storeLoginResponse(loginResponse)

                // Retrieve and assign details
                retrieveLoginData()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun retrieveLoginData() {
        val loginResponse = sharedPreferencesHelper.getLoginResponse()

        loginResponse?.let { response ->
            val loginData = response.status.firstOrNull()

            if (loginData != null) {
                userId = loginData.uuid
                token = loginData.token
                companyId = loginData.companyId

                Toast.makeText(this, "Welcome ${loginData.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login data is missing.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Please Logout and Login Once.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null || sharedPreferencesHelper.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
