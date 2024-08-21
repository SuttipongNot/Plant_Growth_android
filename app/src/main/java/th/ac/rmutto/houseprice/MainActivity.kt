package th.ac.rmutto.houseprice

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

        val editTextAge = findViewById<EditText>(R.id.editTextAge)
        val editTextDistance = findViewById<EditText>(R.id.editTextDistance)
        val editTextMinimart = findViewById<EditText>(R.id.editTextMinimart)
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
                    var message = "ราคาประเมินบ้าน คือ " + data.getString("price") +"บาท/ตารางเมตร"
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("ระบบประเมินราคาบ้าน!!")
                    builder.setMessage(message)
                    builder.setNeutralButton("OK", null)
                    val alert = builder.create()
                    alert.show()

                }
            } else {
                Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
            }
        }

    }
}