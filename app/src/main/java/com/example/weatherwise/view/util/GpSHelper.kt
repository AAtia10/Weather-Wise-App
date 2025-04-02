package com.example.weatherwise.view.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
fun Context.getGpsLocation(onLocationReceived: (location: Location) -> Unit){
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    fusedLocationProviderClient.lastLocation
        .addOnSuccessListener {
            if (it != null) {
                onLocationReceived(it)
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
}