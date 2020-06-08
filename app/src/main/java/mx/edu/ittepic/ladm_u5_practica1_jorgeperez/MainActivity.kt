package mx.edu.ittepic.ladm_u5_practica1_jorgeperez

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data>()
    var REQUEST_PERMISOS = 111
    lateinit var locacion : LocationManager
    var pos1 : Location = Location("")
    var pos2 : Location = Location("")


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Dar permisos
        solicitarPermisos()

        baseRemota.collection("tecnologico")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    lblUbicaciones.setText("ERROR: "+firebaseFirestoreException.message)
                    return@addSnapshotListener
                }

                var resultado = ""
                posicion.clear()
                for(document in querySnapshot!!){
                    var data = Data()
                    data.nombre = document.getString("nombre").toString()
                    data.posicion1 = document.getGeoPoint("posicion1")!!
                    data.posicion2 = document.getGeoPoint("posicion2")!!
                    data.dentro = document.getString("dentro").toString()

                    resultado += data.toString()+"\n\n"
                    posicion.add(data)
                }

                lblUbicaciones.setText("UBICACIONES:\n"+resultado)
            }


        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente = Oyente(this)
        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, oyente)

        btnBuscar.setOnClickListener {
            baseRemota.collection("tecnologico")
                .whereEqualTo("nombre", txtBuscar.getText().toString())
                .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        lblBuscar.setText("ERROR, NO HAY CONEXIÓN CON LA BD")
                        return@addSnapshotListener
                    }


                    var dentro = ""

                    for(document in  querySnapshot!!){
                        pos1.longitude = document.getGeoPoint("posicion1")!!.longitude
                        pos1.latitude = document.getGeoPoint("posicion1")!!.latitude

                        pos2.longitude = document.getGeoPoint("posicion2")!!.longitude
                        pos2.latitude = document.getGeoPoint("posicion2")!!.latitude
                        dentro = document.getString("dentro")!!
                    }

                    var r = "COORDENADAS:\n(${(pos1.latitude)}, ${pos1.longitude}),(${pos2.latitude}, ${pos2.longitude})"
                    r = r + "\nDentro: ${dentro}"
                    lblBuscar.setText(r)
                }
        }

    }

    // FUNCIÓN PARA SOLICITAR LOS PERMISOS NECESARIOS
    private fun solicitarPermisos() {
        var permisoAccessFind = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(permisoAccessFind != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_PERMISOS)
        }
    }

}

class Oyente(puntero:MainActivity) : LocationListener {

    var p = puntero

    override fun onLocationChanged(location: Location) {
        p.lblActual.setText("Ubicación actual:\n${location.latitude}, ${location.longitude}")
        p.lblEstas.setText("")
        var geoPosicionGPS = GeoPoint(location.latitude, location.longitude)

        for (item in p.posicion) {
            if (item.estoyEn(geoPosicionGPS)) {
                p.lblEstas.setText("ESTÁS EN: ${item.nombre}")
            }else{
                p.lblEstas.setText("No estás en ningún punto registrado")
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }
}