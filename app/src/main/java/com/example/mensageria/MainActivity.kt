package com.example.mensageria

import DatastoreRepository.getLoggedState
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mensageria.presenter.ContactMenuActivity
import com.example.mensageria.presenter.RegisterLoginActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.makeText(this, "Validando sessão...", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            if (getLoggedState(this@MainActivity)) {
                startActivity(Intent(this@MainActivity, ContactMenuActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@MainActivity, RegisterLoginActivity::class.java))
            }
        }

    }

}