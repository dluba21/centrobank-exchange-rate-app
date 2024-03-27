package com.example.centrobankrf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.centrobankrf.adapter.CurrencyAdapter
import com.example.centrobankrf.api.RetrofitClient
import com.example.centrobankrf.model.CurrencyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.main_recycler)
        progressBar = findViewById(R.id.main_progress)

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            updateData()
            handler.postDelayed(runnable, 30000) // 30000 миллисекунд = 30 секунд
        }

        handler.post(runnable)
    }

    private fun updateData() {
        progressBar.visibility = ProgressBar.VISIBLE
        RetrofitClient.instance.getCurrencies().enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                progressBar.visibility = ProgressBar.GONE
                if (response.isSuccessful) {
                    val currencies = response.body()?.Valute?.values?.toList()
                    recyclerView.adapter = CurrencyAdapter(currencies ?: emptyList())
                } else {
                    Toast.makeText(this@MainActivity, "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this@MainActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable) // Остановить обновление при свертывании приложения
    }

    override fun onResume() {
        super.onResume()
        handler.post(runnable) // Возобновить обновление при восстановлении приложения
    }
}