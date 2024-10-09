package com.example.searchapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private var searchWithYoutube = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputLayout = findViewById<TextInputLayout>(R.id.inputLayout)
        val inputField = findViewById<EditText>(R.id.inputField)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val switchButton = findViewById<SwitchCompat>(R.id.switch1)


        switchButton.setOnCheckedChangeListener{_, isChecked ->

            searchWithYoutube = isChecked


        }



        searchButton.setOnClickListener {
            val inputText = inputField.text.toString().trim()

            if (inputText.isEmpty()) {
                inputLayout.error = "Input cannot be empty"
            } else {
                inputLayout.error = null
                handleInput(inputText)
            }
        }

    }

    private fun handleInput(input: String) {
        when {
            isValidPhoneNumber(input) -> openPhoneDialer(input)
            isValidEmail(input) -> openEmailClient(input)
            else -> if(searchWithYoutube)   youtube_openUrl(input)
                    else    google_openUrl(input)
        }
    }


    private fun isValidPhoneNumber(phone: String): Boolean {
        val digitsOnly = phone.replace(Regex("[^0-9]"), "")
        return digitsOnly.length >= 10 && Patterns.PHONE.matcher(phone).matches()
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun google_openUrl(input: String) {

        val formattedUrl = if (Patterns.WEB_URL.matcher(input).matches()) {
            if (!input.startsWith("http://") && !input.startsWith("https://")) {
                "https://$input"
            } else {
                input
            }
        } else {
            // Treat it as a search query
            "https://www.google.com/search?q=${input.replace(" ", "+")}"
        }

        OpenUrl(formattedUrl)

    }

    private fun youtube_openUrl(input: String) {

        val formattedUrl = if (Patterns.WEB_URL.matcher(input).matches()) {
            if (!input.startsWith("http://") && !input.startsWith("https://")) {
                "https://$input"
            } else {
                input
            }
        } else {
            // Treat it as a search query
            "https://www.youtube.com/results?search_query=${input.replace(" ", "+")}"
        }

        OpenUrl(formattedUrl)
    }

    private fun OpenUrl(url : String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        try{
            startActivity(browserIntent)
        } catch (e : ActivityNotFoundException){
            Toast.makeText(this, "No app available to open the URL", Toast.LENGTH_SHORT).show()

        }  catch(e : Exception){
            Toast.makeText(this, "Error opening the URL: $url", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPhoneDialer(phone: String) {
        val formattedPhone = "tel:${phone.replace(Regex("[^0-9+]"), "")}"
        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(formattedPhone))
        try {
            startActivity(dialIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No dialer app available or invalid phone number: $phone", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening dialer for number: $phone", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openEmailClient(email: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No email client available or invalid email address: $email", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening email client for: $email", Toast.LENGTH_SHORT).show()
        }
    }
}
