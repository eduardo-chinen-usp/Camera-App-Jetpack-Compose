package br.usp.iee.chinen.digitaltagcamerabyeduardochinen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import br.usp.iee.chinen.digitaltagcamerabyeduardochinen.ui.theme.Purple500
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun CamerOpen(directory: File, myviewmodel: MyViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    SimpleCameraPreview(
        myviewmodel,
        modifier = Modifier.fillMaxSize(),
        context = context,
        lifecycleOwner = lifecycleOwner,
        outputDirectory = directory,
        onMediaCaptured = { url -> }
    )
}

@Composable
fun SimpleCameraPreview(
    myviewmodel: MyViewModel,
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    outputDirectory: File,
    onMediaCaptured: (Uri?) -> Unit

) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    //val camera: Camera? = null
    //var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    //var flashEnabled by remember { mutableStateOf(false) }
    //var flashRes by remember { mutableStateOf(R.drawable.ic_outline_flashlight_off) }
    val executor = ContextCompat.getMainExecutor(context)
    //var cameraSelector: CameraSelector?
    val cameraProvider = cameraProviderFuture.get()

    Box {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({

                    imageCapture = ImageCapture.Builder()
                        .setTargetRotation(previewView.display.rotation)
                        .build()

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageCapture,
                        preview
                    )
                }, executor)
                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                previewView
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,

            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(5.dp),
                backgroundColor = Color(0x60101010),
                elevation = 5.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {


                        //Ativa/Desativa a numeracao no nome do arquivo da foto
                        Switch(
                            checked = myviewmodel.swNomeDoArquivoSemNumero,
                            onCheckedChange = { myviewmodel.swNomeDoArquivoSemNumero = it },
                            colors = SwitchDefaults.colors(Color.Green)
                        )

                        if(myviewmodel.swNomeDoArquivoSemNumero) {
                            myviewmodel.gerarNomeDoArquivoDeFoto()
                            //Toast.makeText(context, "Foto sem número da amostra", Toast.LENGTH_SHORT).show()
                        }else{
                            //Toast.makeText(context, "Foto com número da amostra", Toast.LENGTH_SHORT).show()
                            myviewmodel.gerarNomeDoArquivoDeFoto()
                        }

                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                modifier = Modifier
                                    //.background(color = Color.Cyan)
                                    .padding(3.dp),
                                text = myviewmodel.textoDoAppLinha01,
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                color = Color.White
                            )

                            Text(
                                modifier = Modifier
                                    //.background(color = Color.Cyan)
                                    .padding(3.dp),
                                text = myviewmodel.textoDoAppLinha02,
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                color = Color.White
                            )


                        }
                    }
                }
            }

        }



        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Purple500, RoundedCornerShape(15.dp))
                .padding(8.dp)
                .align(Alignment.BottomCenter)
        ) {

            //Botao Anterior
            IconButton(
                onClick = {
                    myviewmodel.btnAnterior()
                }
            ) {
                Icon(Icons.Filled.ArrowBack,"", Modifier.scale(2f), tint = Color.Cyan)
            }

            //Botao Reset
            IconButton(
                onClick = {
                    myviewmodel.btnReset()
                }
            ) {
                Icon(Icons.Filled.Refresh,"", Modifier.scale(2f), tint = Color.Cyan)
            }

            //Botao Coletar Foto
            IconButton(
                onClick = {
                    myviewmodel.gerarNomeDoArquivoDeFoto()
                    val imgCapture = imageCapture ?: return@IconButton
                    val photoFile = File(
                        outputDirectory,
                        myviewmodel.nomeDoArquivoDeFoto + ".jpg"
                    )
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    imgCapture.takePicture(
                        outputOptions,
                        executor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                onMediaCaptured(Uri.fromFile(photoFile))
                                Toast.makeText(context, "Foto Coletada", Toast.LENGTH_SHORT).show()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Toast.makeText(
                                    context,
                                    "Houston We Have A Problem",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            ) {
                Icon(Icons.Filled.PhotoCamera,"", Modifier.scale(2f), tint = Color.White)
            }
            //Botao Modo de Operacao
            IconButton(
                onClick = {
                    myviewmodel.btnSelecionarModoDeOperacao()
                    Toast.makeText(context, myviewmodel.txtModoDeOperacao, Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Filled.Menu,"", Modifier.scale(2f), tint = Color.Cyan)
            }

            //Botao Proximo
            IconButton(
                onClick = {
                    myviewmodel.btnProximo()
                }
            ) {
                Icon(Icons.Filled.ArrowForward,"", Modifier.scale(2f), tint = Color.Cyan)
            }




        }
    }
}

/*
      IconButton(
          onClick = {
              camera?.let {
                  if (it.cameraInfo.hasFlashUnit()) {
                      flashEnabled = !flashEnabled
                      flashRes = if (flashEnabled) R.drawable.ic_outline_flashlight_on else
                          R.drawable.ic_outline_flashlight_off
                      it.cameraControl.enableTorch(flashEnabled)

                  }
              }
              Toast.makeText(context, "Flashlight ON/OFF", Toast.LENGTH_SHORT).show()
          }
      ) {
          Icon(
              painter = painterResource(id = flashRes),
              contentDescription = "",
              modifier = Modifier.size(35.dp),
              tint = MaterialTheme.colors.surface
          )
      }
      */