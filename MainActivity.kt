package br.usp.iee.chinen.digitaltagcamerabyeduardochinen


import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import java.io.File
import br.usp.iee.chinen.digitaltagcamerabyeduardochinen.ui.theme.DigitalTAGCameraByEduardoChinenTheme
import java.text.SimpleDateFormat


class MainActivity : ComponentActivity() {
    private val myviewmodel by viewModels<MyViewModel>()

    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DigitalTAGCameraByEduardoChinenTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Navigation(myviewmodel = myviewmodel, getDirectory())
                }
            }
        }
    }

    //Store the capture image
    private fun getDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
}


@ExperimentalPermissionsApi
@Composable
fun My_Gui(myviewmodel: MyViewModel, directory: File) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OptionMenu(myviewmodel)
        Spacer(modifier = Modifier.height(2.dp))
        CamerOpen(directory, myviewmodel)
    }
}


@ExperimentalPermissionsApi
@Composable
fun OptionMenu(myviewmodel: MyViewModel) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    TopAppBar(
        modifier = Modifier.height(30.dp),
        title = {
            Text(
                text = "Camera App by Eduardo Chinen ",
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic
            )
        },
        actions = {
            IconButton(onClick = { myviewmodel.showMenu = !myviewmodel.showMenu }) {
                Icon(Icons.Default.MoreVert, "")
            }
            DropdownMenu(
                expanded = myviewmodel.showMenu,
                onDismissRequest = { myviewmodel.showMenu = false }
            ) {
                //Primeiro Item
                DropdownMenuItem(onClick = {
                    cameraPermissionState.launchPermissionRequest()
                    myviewmodel.showMenu = false
                    Toast.makeText(context, "Permission Pressed", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "Pedido de Acesso à câmera", fontSize = 10.sp)
                }
                //Segundo Item
                DropdownMenuItem(onClick = {

                    myviewmodel.openDialog = true
                    myviewmodel.showMenu = false

                    Toast.makeText(context, "Dados Pressed", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "Dados do Ensaio", fontSize = 10.sp)
                }
            }
        }
    )
    //Preencher dados do Ensaio
    if(myviewmodel.openDialog){
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                myviewmodel.openDialog = false
            },
            title = {
                Text(text = "Preencher Dados\n")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                    //verticalArrangement = Arrangement.SpaceAround
                ) {
                    TextField(
                        value = myviewmodel.interessado,
                        onValueChange = { myviewmodel.interessado = it },
                        label = { Text("Nome do Interessado") },
                        singleLine = true,
                        textStyle = TextStyle(
                            //color = Color.Cyan,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(20.dp)
                    )
                    TextField(
                        value = myviewmodel.ordemDeServico,
                        onValueChange = { myviewmodel.ordemDeServico = it },
                        label = { Text("Número da O.S. YYYYxxxxx") },
                        singleLine = true,
                        textStyle = TextStyle(
                            //color = Color.Cyan,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(20.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        myviewmodel.openDialog = false
                    }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        myviewmodel.openDialog = false
                    }) {
                    Text("Cancelar")
                }
            }
        )
    }

}


@ExperimentalPermissionsApi
@Composable
fun Navigation(myviewmodel: MyViewModel = viewModel(), directory: File) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("main_screen") {
            My_Gui(myviewmodel, directory)
        }
    }
}


@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        Animatable(0.1f)
    }
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(1500L)
        navController.navigate("main_screen")
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xffb2fefa),
                        Color(0xff0ed2f7)
                        //Color(0xff4b79a1),
                        //Color(0xff283e51)
                    ),
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_iee_logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}

