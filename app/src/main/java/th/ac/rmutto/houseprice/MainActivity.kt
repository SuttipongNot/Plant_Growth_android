package th.ac.rmutto.houseprice

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.health.connect.datatypes.units.Temperature
import android.os.Bundle
import android.os.StrictMode
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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

    lateinit var spinnerSoilType: Spinner
    lateinit var editTextSunlightHours: EditText
    lateinit var spinnerWaterFequency: Spinner
    lateinit var spinnerFertilizerType: Spinner
    lateinit var editTextTemperature: EditText
    lateinit var editTextHumidity: EditText



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

        spinnerSoilType = findViewById(R.id.spinnerSoilType)
        editTextSunlightHours = findViewById(R.id.editTextSunlightHours)
        spinnerWaterFequency = findViewById(R.id.spinnerWaterFrequency)
        spinnerFertilizerType = findViewById(R.id.spinnerFertilizerType)
        editTextTemperature = findViewById(R.id.editTextTemperature)
        editTextHumidity = findViewById(R.id.editTextHumidity)

        val btnPredict = findViewById<Button>(R.id.btnPredict)

        val  adapSoilType = ArrayAdapter.createFromResource(
            this,
            R.array.SoilType,
            android.R.layout.simple_spinner_item
        )
        adapSoilType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSoilType.setAdapter(adapSoilType)

        val  adapWaterFrequency = ArrayAdapter.createFromResource(
            this,
            R.array.WaterFrequency,
            android.R.layout.simple_spinner_item
        )
        adapWaterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWaterFequency.setAdapter(adapWaterFrequency)

        val  adapFertilizerType = ArrayAdapter.createFromResource(
            this,
            R.array.FertilizerType,
            android.R.layout.simple_spinner_item
        )
        adapFertilizerType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFertilizerType.setAdapter(adapFertilizerType)

        btnPredict.setOnClickListener {
            if (editTextSunlightHours.text.isEmpty() || editTextTemperature.text.isEmpty() || editTextHumidity.text.isEmpty()){
                Toast.makeText(applicationContext, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val url: String = getString(R.string.root_url)

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("Soil_Type", spinnerSoilType.selectedItemId.toString())
                .add("Sunlight_Hours", editTextSunlightHours.text.toString())
                .add("Water_Frequency", spinnerWaterFequency.selectedItemId.toString())
                .add("Fertilizer_Type", spinnerFertilizerType.selectedItemId.toString())
                .add("Temperature", editTextTemperature.text.toString())
                .add("Humidity", editTextHumidity.text.toString())
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val data = JSONObject(response.body!!.string())
                if (data.length() > 0) {
                    val Growth_Milestone = data.getInt("Growth_Milestone")
                    val message = "ผลของการเจริญเติบโตของพืช คือ $Growth_Milestone "
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("ระบบทำนายการเจริญเติบโตของพืช")
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
            spinnerSoilType.setSelection(0)
            editTextSunlightHours.text.clear()
            spinnerWaterFequency.setSelection(0)
            spinnerFertilizerType.setSelection(0)
            editTextTemperature.text.clear()
            editTextHumidity.text.clear()
        }
    }

}//main class