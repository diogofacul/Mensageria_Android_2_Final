package com.example.mensageria.presenter

import DatastoreRepository
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mensageria.R
import com.example.mensageria.data.RecoveryLoginRepository
import com.example.mensageria.data.model.Phone
import kotlinx.coroutines.launch
import postRequest

class RegisterLoginActivity : AppCompatActivity() {

    private lateinit var phone: EditText
    private lateinit var loginButton: Button
    private lateinit var phoneRegex: TextWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_login)
        prepareView()
    }

    private fun initElements(){
        phone = findViewById(R.id.userPhone)
        loginButton = findViewById(R.id.nextButton)
        phoneRegex = genPhoneTextWatcher()
    }

    private fun prepareView() {
        initElements()

        phone.addTextChangedListener(phoneRegex)
        loginButton.setOnClickListener(genLoginButtonOnClickListener())
    }

    private fun genLoginButtonOnClickListener(): OnClickListener {
        return OnClickListener {
            Toast.makeText(this@RegisterLoginActivity, "Enviando SMS, aguarde...", Toast.LENGTH_SHORT).show()

            hideKeyboard()
            lifecycleScope.launch {
                val login : String = DatastoreRepository.getLoginToken(this@RegisterLoginActivity)
                try {
                    val repository = RecoveryLoginRepository()
                    val result = repository.genRecoveryLogin(Phone(phone.text.toString()).getPhone(), login)
                    Log.i("RegisterLoginActivity", result.toMap().toString())
                    if(login != result.get_uuid()){
                        DatastoreRepository.saveLoginToken(this@RegisterLoginActivity, result.get_uuid())
                        DatastoreRepository.saveSMSCode(this@RegisterLoginActivity, result.get_recovery_login_code())
                        DatastoreRepository.savePhone(this@RegisterLoginActivity,result.get_Phone().getPhone())
                        Log.i("RegisterLoginActivity",
                            DatastoreRepository.getSMSCode(this@RegisterLoginActivity).toString()
                        )
                    }
                    postRequest(result.get_Phone().getPhone(), result.get_recovery_login_code(),result.get_uuid())
                    startActivity(Intent(this@RegisterLoginActivity, SMSRecoveryActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterLoginActivity, "Não foi possível enviar SMS...", Toast.LENGTH_SHORT).show()
                    Log.e("RegisterLoginActivity", "Error fetching recovery login", e)
                }
            }
            Log.i("RegisterLoginActivity", "Login button clicked")
        }
    }

    private fun genPhoneTextWatcher(): TextWatcher {
        return object : TextWatcher {

            private var finalPhoneNumber: String = ""
            private var change: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                try {
                    var rawPhone: String = phone.text.toString().replace(Regex("[^0-9]"), "")
                    if (rawPhone.length >= 2) {
                        if (rawPhone.substring(0, 2) == "55") {
                            rawPhone = rawPhone.substring(2)
                        }
                    }

                    val lenPhone: Int = rawPhone.length
                    var ddd = ""
                    var first = ""
                    var last = ""

                    if (lenPhone >= 1) {
                        ddd = if (lenPhone >= 3) {
                            rawPhone.substring(0, 2)
                        } else {
                            rawPhone.substring(0, lenPhone)
                        }
                    }
                    if (lenPhone >= 2) {
                        first = if (lenPhone >= 8) {
                            rawPhone.substring(2, 7)
                        } else {
                            rawPhone.substring(2, lenPhone)
                        }
                    }
                    if (lenPhone >= 8) {
                        last = rawPhone.substring(7, lenPhone)
                    }

                    if (lenPhone > 2) {
                        if (last.isNotEmpty() && !change) {
                            last = "-$last"
                        }

                        finalPhoneNumber = "+55 (${ddd}) $first$last".trim()
                        Log.i("RegisterLoginActivity", finalPhoneNumber)

                        change = finalPhoneNumber != phone.text.toString()
                    }
                } catch (e: Exception) {
                    Log.i("RegisterLoginActivity", getString(R.string.phone_parser_error))
                } finally {
                    if (change) {
                        change = false
                        phone.setText(finalPhoneNumber)
                        phone.setSelection(phone.length())
                        Log.i("RegisterLoginActivity", finalPhoneNumber)
                    }
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = currentFocus
        Log.i("hideKeyboard", currentFocus.toString())
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        } else {
            inputMethodManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
    }

}
