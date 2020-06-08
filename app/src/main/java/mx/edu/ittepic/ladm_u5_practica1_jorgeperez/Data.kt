package mx.edu.ittepic.ladm_u5_practica1_jorgeperez

import com.google.firebase.firestore.GeoPoint

class Data{
    var nombre : String = ""
    var posicion1 : GeoPoint = GeoPoint(0.0, 0.0)
    var posicion2 : GeoPoint = GeoPoint(0.0, 0.0)
    var dentro : String = ""

    override fun toString(): String {
        return nombre+"\n"+posicion1.latitude+","+posicion1.longitude+"\n"+
                posicion2.latitude+","+posicion2.longitude + "\n" + dentro
    }

    fun estoyEn(posicionActual:GeoPoint) : Boolean {
        // LA LÓGICA ES SIMILAR A FIGURA GEOMÉTRICA DE CANVAS
        // ESTOYEN ES MUY SIMILAR O IGUAL AL MÉTODO ESTÁENÁREA DE FIGURA GEOMÉTRICA
        if (posicionActual.latitude >= posicion1.latitude &&
            posicionActual.latitude <= posicion2.latitude){
            if (invertir(posicionActual.longitude) >= invertir(posicion1.longitude) &&
                invertir(posicionActual.longitude) <= invertir(posicion2.longitude)
            ) {
                return true
            }
        }
        return false
    }

    private fun invertir(valor:Double):Double{
        return valor*-1
    }
}