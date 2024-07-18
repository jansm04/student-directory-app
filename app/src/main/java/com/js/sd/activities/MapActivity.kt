package com.js.sd.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.js.sd.R
import com.js.sd.activities.constants.Keys
import com.js.sd.activities.constants.Requests
import com.js.sd.activities.constants.Tabs
import com.js.sd.exceptions.PermissionDeniedException
import com.js.sd.model.Student
import com.js.sd.properties.Properties
import com.js.sd.util.Formatman
import com.js.sd.util.Logman
import com.squareup.picasso.Callback
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Credentials.basic
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class MapActivity : SDActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap


    private var markerStudentMap = mutableMapOf<Marker, Student?>()
    private var markerImageMap = mutableMapOf<Marker, ImageView?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Logman.logInfoMessage("Starting map activity...")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Keys.MAPVIEW_BUNDLE_KEY)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        Logman.logInfoMessage("Loading map view...")
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setCheckedTab(Tabs.MAP)
        moveTaskToBackgroundOnBack()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        Logman.logInfoMessage("Fetching last known location...")
        if (checkPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        Logman.logErrorMessage("Null location.")
                    } else {
                        Logman.logInfoMessage("Location: " + location.latitude + ", " + location.longitude)
                        setMarker(location)
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Requests.REQUEST_FINE_LOCATION
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            onRequestPermissionsResult(
                requestCode,
                grantResults,
                Requests.REQUEST_FINE_LOCATION
            ) { this.getLastLocation() }
        } catch (e: PermissionDeniedException) {
            e.logErrorMessage()
        }
    }

    private fun setMarker(location: Location) {
        val latitude    = location.latitude
        val longitude   = location.longitude

        val coordinates = LatLng(latitude, longitude)
        val marker = map.addMarker(MarkerOptions().position(coordinates).title("Marker"))
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinates))

        // store user location as null student
        marker?.let { markerStudentMap[it] = null }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(Keys.MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(Keys.MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mapView.onSaveInstanceState(mapViewBundle)
    }

    private fun initMaps() {
        val students = initStudents(intent) ?: return

        for (student in students) {
            if (student != null) {
                val marker = map.addMarker(
                    MarkerOptions().position(LatLng(student.latitude, student.longitude))
                )
                marker?.let {
                    markerStudentMap[it] = student

                    // if image path is not empty, load image and add it to the marker-image map
                    if (student.image.isNotEmpty())
                        addImageToMap(marker, student.image)
                }

            }
        }
    }

    private fun initStudents(intent: Intent): Array<Student?>? {
        val names       = intent.getStringArrayExtra("names")
        val ids         = intent.getIntArrayExtra("ids")
        val addresses   = intent.getStringArrayExtra("addresses")
        val latitudes   = intent.getDoubleArrayExtra("latitudes")
        val longitudes  = intent.getDoubleArrayExtra("longitudes")
        val phones      = intent.getStringArrayExtra("phones")
        val images      = intent.getStringArrayExtra("images")

        if (names != null && ids != null && addresses != null && latitudes != null && longitudes != null && phones != null) {
            val size = names.size
            val students = arrayOfNulls<Student>(size)
            for (i in 0 until size) {
                students[i] = Student(
                    names[i],
                    ids[i],
                    addresses[i],
                    latitudes[i],
                    longitudes[i],
                    phones[i],
                    images?.get(i) ?: "",
                    null.toString()
                )
            }
            return students
        }
        return null
    }

    private fun addImageToMap(marker: Marker, imageUrl: String) {
        val imageView = ImageView(this)
        val picasso = getConfiguredPicasso()
        picasso.load(imageUrl).into(imageView, object : Callback {
            override fun onSuccess() {
                imageView.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                markerImageMap[marker] = imageView
            }

            override fun onError(e: Exception) {
                Logman.logErrorMessage("Failed to fetch image from URL.")
            }
        })
    }

    private fun getConfiguredPicasso(): Picasso {
        val picasso = getPicassoBuild()
        picasso.setIndicatorsEnabled(true)
        picasso.isLoggingEnabled = true
        return picasso
    }

    private fun getPicassoBuild(): Picasso {
        val credential = basic(Properties.USERNAME, Properties.PASSWORD)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", credential)
                val request = requestBuilder.build()
                chain.proceed(request)
            })
            .build()

        return Picasso.Builder(applicationContext)
            .downloader(OkHttp3Downloader(okHttpClient))
            .build()
    }

    private fun createInfoWindowAdapter() {
        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker): View? {
                // use default info window frame
                return null
            }

            override fun getInfoWindow(marker: Marker): View {
                val view: LinearLayout
                val student = markerStudentMap[marker]

                // if student is null, then marker maps to user's location
                if (student == null) {
                    view = layoutInflater.inflate(R.layout.user_info_window, null) as LinearLayout
                    fillUserView(marker, view)
                } else {
                    view = layoutInflater.inflate(R.layout.student_info_window, null) as LinearLayout
                    fillStudentView(marker, view, student)
                }
                return view
            }
        })
    }

    private fun fillUserView(marker: Marker, view: LinearLayout) {
        Logman.logInfoMessage("Displaying user info window....")
        val coordinates = view.findViewById<TextView>(R.id.coordinates)
        val position = marker.position
        val roundedLatitude = Math.round(position.latitude * 100).toDouble() / 100
        val roundedLongitude = Math.round(position.longitude * 100).toDouble() / 100
        coordinates.text = Formatman.getCoordinatesText(roundedLatitude, roundedLongitude)
    }

    private fun fillStudentView(marker: Marker, view: LinearLayout, student: Student) {
        Logman.logInfoMessage("Displaying student info window for {}...", student.name)
        setText(view, student)
        val image = markerImageMap[marker]
        if (image != null) {
            setImage(view, image)
        }
    }

    private fun setText(view: LinearLayout, student: Student) {
        val title       = view.findViewById<TextView>(R.id.title)
        val id          = view.findViewById<TextView>(R.id.id)
        val address     = view.findViewById<TextView>(R.id.address)
        val phone       = view.findViewById<TextView>(R.id.phone)
        val coordinates = view.findViewById<TextView>(R.id.coordinates)

        title.text          = student.name
        id.text             = Formatman.getIdText(student.studentId)
        address.text        = student.address
        phone.text          = Formatman.getFormattedNumber(student.phone)
        coordinates.text    = Formatman.getCoordinatesText(student.latitude, student.longitude)
    }

    private fun setImage(view: LinearLayout, image: ImageView) {
        if (image.parent != null) {
            val parent = image.parent as ViewGroup
            parent.removeView(image)
        }
        view.addView(image)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        getLastLocation()
        initMaps()
        createInfoWindowAdapter()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun handleHomeButton(item: MenuItem) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun handleWebButton(item: MenuItem) {
        startActivityWithSameData(WebActivity::class.java)
    }

}