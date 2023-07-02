package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.example.weatherapp.databinding.ActivityMainBinding
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.jvm.internal.Intrinsics.Kotlin

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var weatherApi:WeatherApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpWeatherApi()

        setWeather(City.TOKYO.id)

        binding.osakaButton.setOnClickListener {
            setWeather(City.OSAKA.id)
        }
        binding.hokkaidoButton.setOnClickListener {
            setWeather(City.HOKKAIDO.id)
        }
        binding.nagoyaButton.setOnClickListener {
            setWeather(City.NAGOYA.id)
        }




    }

    fun setUpWeatherApi(){
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val httpLogging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClientBuilder = OkHttpClient.Builder().addInterceptor(httpLogging).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://weather.tsukumijima.net/api/forecast/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(httpClientBuilder)
            .build()

        weatherApi = retrofit.create(WeatherApiService::class.java)
    }

    fun setWeather(cityId: String){
        val imageLoader = ImageLoader.Builder(this)
            .components{add(SvgDecoder.Factory())}
            .build()

        lifecycleScope.launch{
            kotlin.runCatching {
                weatherApi.getCityWeather(cityId)
            }.onSuccess{
                binding.cityTextView.text = it.title
                binding.weatherImageView.load(it.forecast[0].images.url, imageLoader)
            }.onFailure {
                Log.d("error",it.message.toString())
            }
        }
    }
}