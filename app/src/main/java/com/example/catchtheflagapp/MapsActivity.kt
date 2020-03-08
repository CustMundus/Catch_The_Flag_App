package com.example.catchtheflagapp

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val USER_LOCATION_REQUEST_CODE = 1000
    private var playerLocation: Location? = null
    private var oldLocationOfPlayer: Location? = null

    private var locationManager: LocationManager? = null
    private var locationListener: PlayerLocationListener? = null

    private var flagCharacters:ArrayList<FlagCharacter> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = PlayerLocationListener()

        requestLocationPermission()
        initializeFlagCharacters()

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     *
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera


    }

    private fun requestLocationPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),USER_LOCATION_REQUEST_CODE)

                return
            }
        }
        accessUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == USER_LOCATION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                accessUserLocation()

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    inner class PlayerLocationListener: LocationListener {

        constructor(){
            playerLocation = Location("MyProvider")
            playerLocation?.latitude = 0.0
            playerLocation?.longitude = 0.0
        }
        override fun onLocationChanged(location: Location?) {
            playerLocation = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("Not yet implemented")
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("Not yet implemented")
        }

        override fun onProviderDisabled(provider: String?) {
            TODO("Not yet implemented")
        }

    }

    private fun initializeFlagCharacters(){

        flagCharacters.add(FlagCharacter("Hello,this is Flag 1","Easiest",R.drawable.c1,53.901170,27.560489))
        flagCharacters.add(FlagCharacter("Hello,this is Flag 2","Easiest",R.drawable.c2,53.886762, 27.538286))
        flagCharacters.add(FlagCharacter("Hello,this is Flag 3","Easiest",R.drawable.c3,53.898878, 27.560806))
        flagCharacters.add(FlagCharacter("Hello,this is Flag 4","Easiest",R.drawable.c4,53.900092, 27.559467))

    }
    private fun accessUserLocation(){
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2f, locationListener!!)

        var newThread = NewThread()
        newThread.start()
    }

    inner class NewThread: Thread{
        constructor(): super(){
            oldLocationOfPlayer = Location("MyProvider")

        }

        override fun run() {
            super.run()

            while (true){

                if (oldLocationOfPlayer?.distanceTo(playerLocation) == 0f){
                    continue
                }

                oldLocationOfPlayer = playerLocation

                try {
                    runOnUiThread {

                        mMap.clear()
                        val pLocation = LatLng(playerLocation!!.latitude, playerLocation!!.longitude)
                        mMap.addMarker(MarkerOptions().position(pLocation).title("Hi, I am the player").snippet("Let's go!").icon(BitmapDescriptorFactory.fromResource(R.drawable.player)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(pLocation))

                        for (flagCharacterIndex in 0.until(flagCharacters.size)){
                            // 0,1,2,3
                            var fc = flagCharacters[flagCharacterIndex]
                            if(fc.isKilled == false){
                                var fcLocation = LatLng(fc.location!!.latitude,fc.location!!.longitude)
                                mMap.addMarker(MarkerOptions()
                                        .position(fcLocation)
                                        .title(fc.titleOfFlag)
                                        .snippet(fc.messege)
                                        .icon(BitmapDescriptorFactory.fromResource(fc.iconOfFlag!!)))
                            }
                            if (playerLocation!!.distanceTo(fc.location) < 1){

                                Toast.makeText(this@MapsActivity,"${fc.titleOfFlag} is eliminated", Toast.LENGTH_SHORT).show()
                                fc.isKilled = true
                                flagCharacters[flagCharacterIndex] = fc
                            }
                        }

                    }
                    //Thread.sleep(500)

                }catch (exception: Exception){
                    exception.printStackTrace()
                }
            }



        }
    }
}
