package br.usp.iee.chinen.digitaltagcamerabyeduardochinen


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat


class MyViewModel : ViewModel() {

    val listaModosDeOperacao = listOf<String>("Três Amostras sem Sufixo", "Três Amostras", "Uma amostra", "Uma amostra sem Sufixo")
    val listaDeSufixos = listOf<String>("", "VERSO", "FRENTE", "ANTES", "APÓS", "APÓS RETIRADA")
    var flagHabilitaSufixo by mutableStateOf(false)
    var idModoDeOperacao by mutableStateOf(0)
    var idSufixo = 0
    var txtAmostras by mutableStateOf("AMOSTRAS")
    var txtSufixo by mutableStateOf(" ")
    var amostraA by mutableStateOf(1)
    var amostraB by mutableStateOf(2)
    var amostraC by mutableStateOf(3)
    var amostraU by mutableStateOf(1)
    var txtModoDeOperacao by mutableStateOf("Modo de Operação")
    var nomeDoArquivoDeFoto by mutableStateOf("")
    var interessado by mutableStateOf("Interessado")
    var ordemDeServico by mutableStateOf("YYYYxxxxxx")
    var openDialog by mutableStateOf(false)
    var showMenu by mutableStateOf(false)
    var swNomeDoArquivoSemNumero by mutableStateOf(true)
    var textoDoAppLinha01 by mutableStateOf("Interessado")
    var textoDoAppLinha02 by mutableStateOf("YYYYxxxxxx")


    fun gerarNomeDoArquivoDeFoto() {
        val mytime = SimpleDateFormat("yyyy-MM-dd - HH-mm-ss").format(System.currentTimeMillis())

        var txtAmostraA = amostraA.toString()
        var txtAmostraB = amostraB.toString()
        var txtAmostraC = amostraC.toString()
        var txtAmostraU = amostraU.toString()


        if(swNomeDoArquivoSemNumero) {
            nomeDoArquivoDeFoto = "$mytime - $interessado - OS_$ordemDeServico"
            textoDoAppLinha01 = "$mytime - $interessado - OS_$ordemDeServico"
            textoDoAppLinha02 = " "
        }else{
            if(idModoDeOperacao == 0){
                nomeDoArquivoDeFoto =  "$mytime - $interessado - OS_$ordemDeServico - $txtAmostras - $txtAmostraA - $txtAmostraB - $txtAmostraC"
                textoDoAppLinha01 = "$mytime - $interessado - OS_$ordemDeServico"
                textoDoAppLinha02 = "$txtAmostras - $txtAmostraA - $txtAmostraB - $txtAmostraC"
            }
            if(idModoDeOperacao == 1){
                nomeDoArquivoDeFoto =  "$mytime - $interessado - OS_$ordemDeServico - $txtAmostras - $txtAmostraA - $txtAmostraB - $txtAmostraC - $txtSufixo"
                textoDoAppLinha01 = "$mytime - $interessado - OS_$ordemDeServico"
                textoDoAppLinha02 = "$txtAmostras - $txtAmostraA - $txtAmostraB - $txtAmostraC - $txtSufixo"
            }
            if(idModoDeOperacao == 2){
                nomeDoArquivoDeFoto =  "$mytime - $interessado - OS_$ordemDeServico - $txtAmostras - $txtAmostraU - $txtSufixo"
                textoDoAppLinha01 = "$mytime - $interessado - OS_$ordemDeServico"
                textoDoAppLinha02 = "$txtAmostras - $txtAmostraU - $txtSufixo"
            }
            if(idModoDeOperacao == 3){
                nomeDoArquivoDeFoto =  "$mytime - $interessado - OS_$ordemDeServico - $txtAmostras - $txtAmostraU"
                textoDoAppLinha01 = "$mytime - $interessado - OS_$ordemDeServico"
                textoDoAppLinha02 = "$txtAmostras - $txtAmostraU"
            }
        }
    }

    fun modoZero(){
        txtModoDeOperacao = listaModosDeOperacao.elementAt(0)
        flagHabilitaSufixo = false
        txtAmostras = "AMOSTRAS"
        txtSufixo = " "
        gerarNomeDoArquivoDeFoto()
    }

    fun modoUm(){
        txtModoDeOperacao = listaModosDeOperacao.elementAt(1)
        idSufixo = 1
        flagHabilitaSufixo = true
        txtAmostras = "AMOSTRAS"
        txtSufixo = "FRENTE"
        gerarNomeDoArquivoDeFoto()
    }

    fun modoDois(){
        txtModoDeOperacao = listaModosDeOperacao.elementAt(2)
        idSufixo = 3
        flagHabilitaSufixo = true
        txtAmostras = "AMOSTRA"
        if(flagHabilitaSufixo){
            txtSufixo = listaDeSufixos.elementAt(idSufixo)
        }
        gerarNomeDoArquivoDeFoto()
    }

    fun modoTres(){
        txtModoDeOperacao = listaModosDeOperacao.elementAt(3)
        flagHabilitaSufixo = false
        txtAmostras = "AMOSTRA"
        txtSufixo = " "
        gerarNomeDoArquivoDeFoto()
    }

    fun btnSelecionarModoDeOperacao(){
        swNomeDoArquivoSemNumero = false
        idModoDeOperacao += 1
        when (idModoDeOperacao){
            0 -> modoZero()
            1 -> modoUm()
            2 -> modoDois()
            3 -> modoTres()
            else -> {
                idModoDeOperacao = 0
                modoZero()
            }
        }
    }

    fun btnProximo(){
        swNomeDoArquivoSemNumero = false
        if (idModoDeOperacao == 0){
            amostraA += 3
            amostraB += 3
            amostraC += 3
        }
        if (idModoDeOperacao == 1){
            if(idSufixo == 1){
                txtSufixo = listaDeSufixos.elementAt(idSufixo)
                idSufixo = 2
            }else{
                txtSufixo = listaDeSufixos.elementAt(idSufixo)
                idSufixo = 1
                amostraA += 3
                amostraB += 3
                amostraC += 3
            }
        }
        if(idModoDeOperacao == 2){
            if(idSufixo < 5) {
                idSufixo += 1
                txtSufixo = listaDeSufixos.elementAt(idSufixo)
            }else{
                idSufixo = 3
                amostraU += 1
                txtSufixo = listaDeSufixos.elementAt(idSufixo)
            }
        }
        if (idModoDeOperacao == 3){
            amostraU += 1
        }
        gerarNomeDoArquivoDeFoto()
    }

    fun btnAnterior(){
        swNomeDoArquivoSemNumero = false
        if(idModoDeOperacao == 0){
            if(amostraA > 1){
                amostraA -= 3
                amostraB -= 3
                amostraC -= 3
            }else{
                amostraA = 1
                amostraB = 2
                amostraC = 3
            }
        }
        if(idModoDeOperacao == 1){
            if (idSufixo == 1){
                txtSufixo = listaDeSufixos.elementAt(idSufixo)
                idSufixo = 2
                if(amostraA > 1){
                    amostraA -= 3
                    amostraB -= 3
                    amostraC -= 3
                }else{
                    amostraA = 1
                    amostraB = 2
                    amostraC = 3
                    txtSufixo = listaDeSufixos.elementAt(idSufixo)
                    idSufixo = 1
                }
            }else{
                txtSufixo = listaDeSufixos.elementAt(idSufixo)
                idSufixo = 1
            }
        }
        if(idModoDeOperacao == 2){
            if (amostraU > 1) {
                if (idSufixo > 3) {
                    idSufixo -= 1
                    txtSufixo = listaDeSufixos.elementAt(idSufixo)
                } else {
                    idSufixo = 5
                    amostraU -= 1
                    txtSufixo = listaDeSufixos.elementAt(idSufixo)
                }
            }else{
                if (idSufixo > 3) {
                    idSufixo -= 1
                    txtSufixo = listaDeSufixos.elementAt(idSufixo)
                }
            }
        }
        if(idModoDeOperacao == 3){
            if(amostraU > 1){
                amostraU -= 1
            }else{
                amostraU = 1
            }
        }
        gerarNomeDoArquivoDeFoto()
    }

    fun btnReset(){
        swNomeDoArquivoSemNumero = true
        flagHabilitaSufixo = false
        idModoDeOperacao = 0
        idSufixo = 0
        txtAmostras = "AMOSTRAS"
        txtSufixo = " "
        amostraA = 1
        amostraB = 2
        amostraC = 3
        amostraU = 1
        gerarNomeDoArquivoDeFoto()
    }
}