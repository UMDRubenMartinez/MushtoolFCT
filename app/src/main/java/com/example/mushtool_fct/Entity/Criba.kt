package com.example.mushtool_fct.Entity

class Criba: Mushroom() {
    var altresNoms: String = ""
    var habitat: String = ""
    var nomConegut: String = ""

    fun cookMushroom() {
        println("Cooking $nomCientific")
    }

    fun setMush(seta: Mushroom){
        this.consum = seta.consum
        this.dificultat = seta.dificultat
        this.diametre = seta.diametre
        this.gruixDePeu = seta.gruixDePeu
        this.llargadaDelPeu = seta.llargadaDelPeu
        this.nomCientific = seta.nomCientific
        this.temporada = seta.temporada
        this.toxicitat = seta.toxicitat
        this.urlFoto = seta.urlFoto
    }
}