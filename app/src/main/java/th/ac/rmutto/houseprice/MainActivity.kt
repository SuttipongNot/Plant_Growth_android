package th.ac.rmutto.houseprice

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    @SuppressLint("DefaultLocale")

    lateinit var editTextAge: EditText
    lateinit var editTextDistance: EditText
    lateinit var editTextMinimart: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //To run network operations on a main thread or as an synchronous task.
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        editTextAge = findViewById(R.id.editTextAge)
        editTextDistance = findViewById(R.id.editTextDistance)
        editTextMinimart = findViewById(R.id.editTextMinimart)
        val btnPredict = findViewById<Button>(R.id.btnPredict)

        btnPredict.setOnClickListener {
            val url: String = getString(R.string.root_url)

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("age", editTextAge?.text.toString())
                .add("distance", editTextDistance?.text.toString())
                .add("minimart", editTextMinimart?.text.toString())
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val data = JSONObject(response.body!!.string())
                if (data.length() > 0) {
                    val price = String.format("%,.0f", data.getDouble("price") * 1000)
                    val message = "ราคาประเมินบ้าน คือ $price บาท/ตารางเมตร"
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("ระบบประเมินราคาบ้าน!!")
                    builder.setMessage(message)
                    builder.setNeutralButton("OK", clearText())
                    val alert = builder.create()
                    alert.show()

                }
            } else {
                Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
            }
        }//button predict
    }//onCreate function

    private fun clearText(): DialogInterface.OnClickListener? {
        return DialogInterface.OnClickListener { dialog, which ->
            editTextAge.text.clear()
            editTextDistance.text.clear()
            editTextMinimart.text.clear()
            editTextAge.requestFocus()
        }
    }

}//main class