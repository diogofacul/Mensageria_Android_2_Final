package com.example.mensageria.presenter

import DatastoreRepository.getSMSCode
import DatastoreRepository.saveLoggedState
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mensageria.R
import kotlinx.coroutines.launch

class SMSRecoveryActivity : AppCompatActivity(){
    private lateinit var smsButton : Button
    private lateinit var sms : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sms_recovery)
        prepareView()
    }
    private fun prepareView() {
        initElements()

        smsButton.setOnClickListener(genSMSButtonOnClickListener())
    }

    private fun genSMSButtonOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            val smstxt = sms.text.toString().toInt()
            lifecycleScope.launch {
                val smssaved = getSMSCode(this@SMSRecoveryActivity)
                Log.i("SMSRecoveryActivity","$smstxt -> $smssaved")
                Toast.makeText(this@SMSRecoveryActivity, "Validando SMS...", Toast.LENGTH_SHORT).show()
                if (smstxt == smssaved) {
                    saveLoggedState(this@SMSRecoveryActivity,true)
                    startActivity(Intent(this@SMSRecoveryActivity, ContactMenuActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@SMSRecoveryActivity, "SMS Invalido...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initElements(){
        sms = findViewById(R.id.userSMS)
        smsButton = findViewById(R.id.nextButtonSMS)
    }


}