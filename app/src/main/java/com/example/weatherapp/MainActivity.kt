package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId =2

    private lateinit var Maintxt : TextView
    lateinit var layoutmanager: LinearLayoutManager
    lateinit var recycleradaptar: Adapter
    lateinit var userView: RecyclerView

    lateinit var searchbtn : ImageButton
    lateinit var placename : EditText

//    private lateinit var text : ArrayList<TextView>
//    private lateinit var texttime : ArrayList<TextView>
//    private lateinit var textrain : ArrayList<TextView>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
        userView = findViewById(R.id.recylerView)
        layoutmanager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        Maintxt = findViewById(R.id.maintext)

        searchbtn = findViewById(R.id.searchbtn)
        placename = findViewById(R.id.Editname)
        placename.visibility = INVISIBLE

        val current = LocalDateTime.now()
        val d1 = current.hour
        
        if(d1>=19|| d1<=5){
            findViewById<RelativeLayout>(R.id.MainScreen).setBackgroundResource(R.drawable.nightimage)
        }

        searchbtn.setOnClickListener {
            if (placename.isVisible){

                val itemList = arrayListOf<weather>()
                recycleradaptar = Adapter(itemList);
                userView.adapter = recycleradaptar
                userView.layoutManager = layoutmanager


                val name = placename.text.toString()
                val geocoder = Geocoder(this, Locale.getDefault())
                val list = geocoder.getFromLocationName(name, 1)
                val lat = list[0].latitude.toFloat()
                val lng = list[0].longitude.toFloat()

                findViewById<TextView>(R.id.txtloc).text = name

//                        https://weather-by-api-ninjas.p.rapidapi.com/v1/weather?city=Kurukshetra&rapidapi-key=8130f1378emshdfa6403c0f108fep149d0fjsn304832ed200f
                val queue = Volley.newRequestQueue(this)
                val url = "https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lng}&hourly=temperature_2m,relativehumidity_2m,precipitation,rain,showers,snowfall,cloudcover&timezone=IST&current_weather=true"
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    { response ->
                        println("response is$response")

                        var i =0;
                        var j =0;
                        while(j<24){
                            Maintxt.text = response.getJSONObject("current_weather").getString("temperature")
                            findViewById<TextView>(R.id.windspeed).text = "Wind Speed = " + response.getJSONObject("current_weather").getString("windspeed").toString() +"Km/hr"

                            val current = LocalDateTime.now()
                            val d1 = current.hour
                            val d = response.getJSONObject("hourly").getJSONArray("time")[j].toString()
                            val time  = LocalDateTime.parse(d)

                            val d2 = time.hour
                            if (d2>=d1){
                                println(d2)
                                val temp = response.getJSONObject("hourly").getJSONArray("temperature_2m")[j].toString()
                                val rn = response.getJSONObject("hourly").getJSONArray("rain")[j]
                                val rain = rn.toString()
                                val check = rain.toIntOrNull()
                                println("check is "+ check)
                                var a  = R.drawable.sunimage
                                if (check != null) {
                                    if (check>=2){
                                        a = R.drawable.rainimage
                                    }
                                }
                                else if(d2>=19 || d2<=5){
                                    a = R.drawable.moonimage
                                }

                                val userobject = weather(
                                    temp + "\u2103",
                                    rain + "mm",
                                    "$d2:00",
                                    a,
                                )

                                itemList.add(userobject)
//                                        text[i].text = temp.toString()
//                                        textrain[i].text =  "${rain}mm"
//                                        texttime[i].text = "$d2:00"
                                println(temp)
                                i++
                            }
                            j++
                        }
                        while(i<24){
                            val d = response.getJSONObject("hourly").getJSONArray("time")[j].toString()
                            val time  = LocalDateTime.parse(d)

                            val d2 = time.hour
                            println("Value of d2 " + d2)
                            val temp = response.getJSONObject("hourly").getJSONArray("temperature_2m")[j].toString()
                            val rn = response.getJSONObject("hourly").getJSONArray("rain")[j]
                            val rain = rn.toString()
                            val check = rain.toIntOrNull()
                            println("check is "+ check)
                            var a  = R.drawable.sunimage
                            if (check != null) {
                                if (check>=2){
                                    a = R.drawable.rainimage
                                }
                            }
                            else if(d2>=19 || d2<=5){
                                a = R.drawable.moonimage
                            }
                            val userobject = weather(
                                temp+"\u2103",
                                rain + "mm" ,
                                "$d2:00",
                                a
                            )

                            itemList.add(userobject)
                            println(temp)
                            i++
                            j++
                        }
                        recycleradaptar = Adapter(itemList);
                        userView.adapter = recycleradaptar
                        userView.layoutManager = layoutmanager


                    },
                    { error ->
                        // TODO: Handle error
                    }
                )

                queue.add(jsonObjectRequest)
                placename.visibility = INVISIBLE
            }
            else{
                val imm =getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                placename.visibility= VISIBLE
            }
        }



}

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        val itemList = arrayListOf<weather>()
                        recycleradaptar = Adapter(itemList);
                        userView.adapter = recycleradaptar
                        userView.layoutManager = layoutmanager
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        println(list[0])
//                        println(list[0].locality)
//                        findViewById<TextView>(R.id.latTextView).text = list[0].adminArea
                        findViewById<TextView>(R.id.txtloc).text = list[0].locality

//                        https://weather-by-api-ninjas.p.rapidapi.com/v1/weather?city=Kurukshetra&rapidapi-key=8130f1378emshdfa6403c0f108fep149d0fjsn304832ed200f
                        val queue = Volley.newRequestQueue(this)
                        val url = "https://api.open-meteo.com/v1/forecast?latitude=${location.latitude}&longitude=${location.longitude}&hourly=temperature_2m,relativehumidity_2m,precipitation,rain,showers,snowfall,cloudcover&timezone=IST&current_weather=true"
                        val jsonObjectRequest = JsonObjectRequest(
                            Request.Method.GET, url, null,
                            { response ->
                                println("response is$response")

                                var i =0;
                                var j =0;
                                while(j<24){
                                    Maintxt.text = response.getJSONObject("current_weather").getString("temperature")
                                    findViewById<TextView>(R.id.windspeed).text = "Wind Speed = " + response.getJSONObject("current_weather").getString("windspeed").toString() +"Km/hr"

                                    val current = LocalDateTime.now()
                                    val d1 = current.hour
                                    val d = response.getJSONObject("hourly").getJSONArray("time")[j].toString()
                                    val time  = LocalDateTime.parse(d)

                                    val d2 = time.hour
                                    if (d2>=d1){
                                        println(d2)
                                        val temp = response.getJSONObject("hourly").getJSONArray("temperature_2m")[j].toString()
                                        val rn = response.getJSONObject("hourly").getJSONArray("rain")[j]
                                        val rain = rn.toString()
                                        val check = rain.toIntOrNull()
                                        println("check is "+ check)
                                        var a  = R.drawable.sunimage
                                        if (check != null) {
                                            if (check>=2){
                                                a = R.drawable.rainimage
                                            }
                                        }
                                        else if(d2>=19 || d2<=5){
                                            a = R.drawable.moonimage
                                        }

                                        val userobject = weather(
                                            temp + "\u2103",
                                            rain + "mm",
                                            "$d2:00",
                                            a,
                                        )

                                        itemList.add(userobject)
//                                        text[i].text = temp.toString()
//                                        textrain[i].text =  "${rain}mm"
//                                        texttime[i].text = "$d2:00"
                                        println(temp)
                                        i++
                                    }
                                    j++
                                }
                                while(i<24){
                                    val d = response.getJSONObject("hourly").getJSONArray("time")[j].toString()
                                    val time  = LocalDateTime.parse(d)

                                    val d2 = time.hour
                                    println("Value of d2 " + d2)
                                    val temp = response.getJSONObject("hourly").getJSONArray("temperature_2m")[j].toString()
                                    val rn = response.getJSONObject("hourly").getJSONArray("rain")[j]
                                    val rain = rn.toString()
                                  val check = rain.toIntOrNull()
                                        println("check is "+ check)
                                        var a  = R.drawable.sunimage
                                        if (check != null) {
                                            if (check>=2){
                                                a = R.drawable.rainimage
                                            }
                                        }
                                        else if(d2>=19 || d2<=5){
                                            a = R.drawable.moonimage
                                        }
                                    val userobject = weather(
                                        temp+"\u2103",
                                        rain + "mm" ,
                                        "$d2:00",
                                        a
                                    )

                                    itemList.add(userobject)
                                    println(temp)
                                    i++
                                    j++
                                }
                                recycleradaptar = Adapter(itemList);
                                userView.adapter = recycleradaptar
                                userView.layoutManager = layoutmanager


                            },
                            { error ->
                                // TODO: Handle error
                            }
                        )

                        queue.add(jsonObjectRequest)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation!!
//            findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
//            findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            permissionId
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}