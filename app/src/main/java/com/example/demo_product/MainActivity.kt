package com.example.demo_product

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.demo_product.ui.theme.Demo_ProductTheme
import com.example.demo_product.ui.theme.PrimaryColor
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    companion object {
        const val IMG_CLICK = 1
    }

    private var imagePath: String = ""
    var bitmap: MutableState<Bitmap?>? = null

    val promotionTrackList: MutableList<PromotionTrack> = mutableListOf()

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    private fun createDummyPromotionTrack() {
        for (i in 0..10) {
            val promotionTrack = PromotionTrack(
                isMandatory = ((i % 2) == 0),
                productTitle = "Title $i",
                offerTitle = "Offer $i"
            )
            promotionTrackList.add(promotionTrack)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        createDummyPromotionTrack()

        super.onCreate(savedInstanceState)
        setContent {
            bitmap = remember {
                mutableStateOf(null)
            }
            Demo_ProductTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn {
                        items(promotionTrackList) {
                            ProductItem(it)
                        }
                    }
                }
            }
        }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
// openCamera(IMG_CLICK_FRONT)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    bitmap?.value = processImageRotation(imagePath)

                } else {
                    Toast.makeText(this@MainActivity, "Camera closed", Toast.LENGTH_SHORT).show()
                }
            }
    }


    @Composable
    fun PromotionTrackingScreen() {

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 10.dp)
        ) {


            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {

                CommonArrowLeft()
                /*Column(Modifier.horizontalScroll(rememberScrollState())) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 0.dp)
                    ) {

                        CommonButton("All", 18.sp, Color.White, PrimaryColor)
                        Spacer(modifier = Modifier.width(10.dp))

                        CommonButton("Godrej No.1", 14.sp, PrimaryColor, Color.White, isBorder = true)
                        Spacer(modifier = Modifier.width(10.dp))

                        CommonButton("Pears", 14.sp, PrimaryColor, Color.White,isBorder = true)
                        Spacer(modifier = Modifier.width(10.dp))

                        CommonButton("Medimix", 14.sp, PrimaryColor, Color.White,isBorder = true)
                    }
                }*/

                MyUI()

            }


            Spacer(modifier = Modifier.height(10.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {

                CommonArrowLeft()

                Column(Modifier.horizontalScroll(rememberScrollState())) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp)
                    ) {

                        CommonButton("All", 18.sp, Color.White, PrimaryColor)
                        Spacer(modifier = Modifier.width(10.dp))

                        CommonButton(
                            "Godrej No.1",
                            14.sp,
                            PrimaryColor,
                            Color.White,
                            isBorder = true
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        CommonButton("Pears", 14.sp, PrimaryColor, Color.White, isBorder = true)
                        Spacer(modifier = Modifier.width(10.dp))

                        CommonButton("Medimix", 14.sp, PrimaryColor, Color.White, isBorder = true)
                    }
                }
            }

        }

    }


    @Composable
    fun CommonButton(
        title: String,
        textFontSize: TextUnit,
        fontColor: Color,
        backgrounColor: Color,
        isBorder: Boolean = false
    ) {
        if (isBorder) {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgrounColor
                ),
                modifier = Modifier
                    .border(1.dp, PrimaryColor, RoundedCornerShape(25.dp))

            ) {
                Text(
                    text = title,
                    fontSize = textFontSize,
                    color = fontColor,
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp)

                )
            }
        } else {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgrounColor
                ),

                ) {
                Text(
                    text = title,
                    fontSize = textFontSize,
                    color = fontColor,

                    )
            }
        }
    }

    @Composable
    fun CommonArrowLeft() {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.baseline_circle_24),
                contentDescription = "",
                modifier = Modifier.size(40.dp)
            )

            IconButton(onClick = { }) {
                Icon(
                    Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Arrow Back",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Red,
                )
            }
        }
    }

    @Composable
    fun CommonArrowRight() {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.baseline_circle_24),
                contentDescription = "",
                modifier = Modifier.size(50.dp)
            )

            IconButton(onClick = { }) {
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Arrow Back",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Red,
                )
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
    @Composable
    fun PromotionTrackingTopBar() {
        Demo_ProductTheme() {
            Scaffold(topBar = {
                TopAppBar(modifier = Modifier.height(50.dp),
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = com.example.demo_product.ui.theme.PrimaryColor
                    ),
                    title = {
                        Text(
                            text = "Promotion Tracking",
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 5.dp, top = 10.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(
                                Icons.Filled.KeyboardArrowLeft,
                                contentDescription = "Arrow Back",
                                modifier = Modifier.size(50.dp),
                                tint = Color.White,
                            )
                        }
                    })
            },

                content = { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding),
                    ) {
                        PromotionTrackingScreen()
                    }
                })
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyUI() {
        val itemsList = listOf("All", "Godrej No.1", "Pears", "Medimix")

//        val contextForToast = LocalContext.current.applicationContext

        var selectedItem = remember {
            mutableStateOf(itemsList[0])
        }

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(itemsList) { item ->
                FilterChip(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .border(1.dp, PrimaryColor, RoundedCornerShape(25.dp)),
                    selected = (item == selectedItem.value),
                    onClick = {
                        selectedItem.value = item
//                        Toast.makeText(contextForToast, selectedItem, Toast.LENGTH_SHORT).show()
                    },
                    label = {
                        Text(
                            text = item,
                            fontSize = 14.sp,
                            color = PrimaryColor
                        )
                    }
                )
            }
        }
    }

    @Composable
    fun ProductItem(promotionTrack: PromotionTrack) {
        val selectedOption = remember { mutableStateOf("") }
        val reason = remember { mutableStateOf("") }
        val type = remember { mutableStateOf("") }

        Column(Modifier.fillMaxSize()) {

            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
//                .height(0.dp)
            ) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(PrimaryColor),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Product",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .weight(0.6f)
                                .padding(top = 8.dp, bottom = 8.dp, start = 85.dp)

                        )

                        Divider(
                            color = Color.Gray, modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )

                        Text(
                            text = "Offers",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .weight(0.4f)
                                .padding(top = 8.dp, bottom = 8.dp, start = 55.dp)

                        )
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .padding(start = 15.dp, end = 10.dp, top = 0.dp, bottom = 0.dp)
                    ) {
                        Text(
                            text = promotionTrack.productTitle,
                            fontSize = 14.sp,
                            color = PrimaryColor,
                            modifier = Modifier
                                .weight(0.6f)
                                .padding(top = 8.dp, bottom = 0.dp, start = 0.dp)

                        )

                        Divider(
                            color = Color.Gray, modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )

                        Text(
                            text = promotionTrack.offerTitle,
                            fontSize = 14.sp,
                            color = PrimaryColor,
                            modifier = Modifier
                                .weight(0.4f)
                                .padding(top = 8.dp, bottom = 8.dp, start = 30.dp)


                        )
                    }

                    Divider(
                        color = Color.Gray, modifier = Modifier
                            .fillMaxWidth()
                            .width(1.dp)
                    )

                    val selectedOptionState = remember(key1 = promotionTrack.productTitle) { mutableStateOf("") }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(35.dp)
                            .padding(top = 0.dp, bottom = 0.dp)
                    ) {
                        RadioButtons(
                            titleName = "Is same Promotion running?",
                            radioButtonName1 = "Yes",
                            radioButtonName2 = "No",
                            selectedOptionState
                        )
                        if (selectedOption.value == "Yes") {
                            promotionTrack.isSamePromotion = true
                            promotionTrack.samePromotionData = SamePromotionData()
                            promotionTrack.samePromotionData?.let { PromotionRunningYes(it) }
                        } else if (selectedOption.value == "No") {
                            promotionTrack.isSamePromotion = false
                            SelectReasonDropDown(
                                value = reason.value,
                                onChange = { reason.value = it })
                        } else {
                            Log.d("PRODUCT_ITEM", "ProductItem: else case")
                        }
                    }


                }
            }
        }
    }

    @Composable
    fun RadioButtons(
        titleName: String,
        radioButtonName1: String,
        radioButtonName2: String,
        radioButtonSelectedValue: MutableState<String>
    ) {
        Row() {
            Text(
                text = titleName,
                modifier = Modifier.padding(start = 25.dp, top = 5.dp, bottom = 5.dp),
                fontSize = 12.sp,
                color = Color.Black,

                )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 0.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                RadioButton(
                    selected = radioButtonSelectedValue.value == radioButtonName1,
                    onClick = {
                        radioButtonSelectedValue.value = radioButtonName1
//                        selectedOption(radioButtonSelectedValue.value)
                    },
                    modifier = Modifier.padding(top = 0.dp, start = 0.dp),
                    colors = RadioButtonDefaults.colors(PrimaryColor)
                )

                Text(
                    text = radioButtonName1,
                    modifier = Modifier.padding(start = 0.dp, top = 0.dp),
                    fontSize = 12.sp,
                    color = Color.Black
                )

                RadioButton(
                    selected = radioButtonSelectedValue.value == radioButtonName2,
                    onClick = {
                        radioButtonSelectedValue.value = radioButtonName2
//                        selectedOption(radioButtonSelectedValue.value)
                    },
                    modifier = Modifier.padding(top = 0.dp, start = 0.dp),
                    colors = RadioButtonDefaults.colors(PrimaryColor)
                )

                Text(
                    text = radioButtonName2,
                    modifier = Modifier.padding(start = 0.dp, top = 0.dp),
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SelectReasonDropDown(value: String, onChange: (String) -> Unit) {
        val expand = remember { mutableStateOf(false) }
        val reasons =
            listOf("Different Promotion Running", "Promo Period Over", "No Stock On Shelf")


        Column(
            Modifier.background(Color.White)
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 5.dp),
                shape = RoundedCornerShape(size = 20.dp),
                border = BorderStroke(width = 1.dp, color = PrimaryColor),
            ) {
                ExposedDropdownMenuBox(expanded = expand.value, onExpandedChange = {
                    expand.value = !expand.value
                }) {

                    TextField(
                        value = (value),
                        textStyle = TextStyle(fontSize = 14.sp),
                        onValueChange = { onChange(it) },
                        placeholder = {
                            Text(
                                text = "Select Reason",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expand.value)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 0.dp)
                            .height(50.dp)
                            .background(Color.White)
                            .menuAnchor()
                            .border(1.dp, PrimaryColor, RectangleShape),

                        shape = RoundedCornerShape(10.dp),

                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                    )

                    ExposedDropdownMenu(expanded = expand.value,
                        onDismissRequest = { expand.value = false }) {
                        reasons.forEach { reason ->
                            DropdownMenuItem(text = {
                                Text(
                                    text = reason,
                                    fontSize = 14.sp,
                                )
                            }, onClick = {
                                onChange(reason)
                                expand.value = false
                            }, modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DifferentPromotionRunning() {
        var promoTypeTextField = remember { mutableStateOf("") }
        var promoType = remember { mutableStateOf("") }
        Column(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {

                SelectPromotionType(value = promoType.value, onChange = { promoType.value = it })

                ClickImage()
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = promoTypeTextField.value,
                onValueChange = { (promoTypeTextField.value) = it },
                placeholder = {
                    Text(
                        text = "Enter Promotion Type", fontSize = 12.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    cursorColor = PrimaryColor
                ),
                modifier = Modifier
                    .padding(top = 5.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
                    .fillMaxWidth()
                    .background(Color.White),

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SelectPromotionType(value: String, onChange: (String) -> Unit) {
        val expand = remember { mutableStateOf(false) }
        val promotionTypes = listOf("Promotion 1", "Promotion 2", "Promotion 3")


        Column(
            Modifier.background(Color.White)
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 20.dp, end = 150.dp, bottom = 5.dp),
                shape = RoundedCornerShape(size = 30.dp),
                border = BorderStroke(width = 1.dp, color = PrimaryColor),
            ) {
                ExposedDropdownMenuBox(expanded = expand.value, onExpandedChange = {
                    expand.value = !expand.value
                }) {

                    TextField(
                        value = (value),
                        textStyle = TextStyle(fontSize = 14.sp),
                        onValueChange = { onChange(it) },
                        placeholder = {
                            Text(
                                text = "Select Promotion Type",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                        },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expand.value)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 0.dp)
                            .height(50.dp)
                            .background(Color.White)
                            .menuAnchor()
                            .border(1.dp, PrimaryColor, RectangleShape),

                        shape = RoundedCornerShape(10.dp),

                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                    )

                    ExposedDropdownMenu(expanded = expand.value,
                        onDismissRequest = { expand.value = false }) {
                        promotionTypes.forEach { type ->
                            DropdownMenuItem(text = {
                                Text(
                                    text = type,
                                    fontSize = 14.sp,
                                )
                            }, onClick = {
                                onChange(type)
                                expand.value = false
                            }, modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ClickImage() {

        Column() {
            Box(
                Modifier
                    .fillMaxWidth()
                /*.clickable {
                    onImageClick(IMG_CLICK)
                }*/,
                contentAlignment = Alignment.Center
            ) {

                val bitmapValue = bitmap?.value

                if (bitmapValue == null) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                            contentDescription = "",
                            Modifier
                                .size(40.dp)
                                .clickable {
                                    onImageClick(IMG_CLICK)
                                })
                    }
                } else {
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = bitmapValue.asImageBitmap(), contentDescription = "",
                            modifier = Modifier
                                .size(80.dp)
                                .clickable {
                                    onImageClick(
                                        IMG_CLICK
                                    )
                                },
                        )
                    }
                }
            }
        }
    }

    private fun onImageClick(imgClicked: Int) {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera(imgClicked)
        } else {
            askCameraPermission()
        }
    }

    private fun askCameraPermission() {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun openCamera(imgClicked: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createPhotoFile()
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }

            if (photoFile != null) {
                val uri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "com.example.demo_product.provider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                if (imgClicked == MainActivity.IMG_CLICK) {
                    imagePath = photoFile.absolutePath
                    cameraLauncher.launch(intent)
                }
            }
        } else {
            Toast.makeText(this@MainActivity, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createPhotoFile(): File {
        val fileTimestamp = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.US).format(Date())
        val fileName = "JPEG_$fileTimestamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    private fun processImageRotation(imagePath: String): Bitmap? {
        val ei = ExifInterface(imagePath)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val bmp = BitmapFactory.decodeFile(imagePath)
        bmp ?: return null

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bmp, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bmp, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bmp, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bmp
            else -> bmp
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    @Composable
    fun PromotionRunningYes(samePromotionData: SamePromotionData) {
        val selectedOption = remember { mutableStateOf("") }

        Column(
            Modifier
                .height(170.dp)
                .fillMaxWidth()
        ) {
            val selectedOption = remember { mutableStateOf("") }
            Row(Modifier.fillMaxWidth()) {
                val selectedOptionState = remember { mutableStateOf("") }
                RadioButtons(
                    titleName = "Same on POS?",
                    radioButtonName1 = "Yes",
                    radioButtonName2 = "No",
                    selectedOptionState
//                selectedOption = {
//                        selectedOption.value = it
//                        if (it == "Yes") {
//                            samePromotionData.isSamePos = true
//                        } else if (it == "No") {
//                            samePromotionData.isSamePos = false
//                        }
//                    }
                )
                if (selectedOption.value == "Yes") {
                    samePromotionData.isSamePos = true
                } else if (selectedOption.value == "No") {
                    samePromotionData.isSamePos = false
                }
            }

            Row(Modifier.fillMaxWidth()) {
                val selectedOptionState = remember { mutableStateOf("") }
                RadioButtons(
                    titleName = "Same on Shelf Taker?",
                    radioButtonName1 = "Yes",
                    radioButtonName2 = "No",
                    selectedOptionState
//                    selectedOption = {
//                        selectedOption.value = it
//                    }
                )
            }

            Spacer(modifier = Modifier.height(0.dp))

            Row(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .height(70.dp)
                        .width(240.dp)
                ) {

                    Row {
                        Text(
                            text = "Count of Facings:",
                            fontSize = 10.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(Color.White),
                            border = BorderStroke(width = 1.dp, color = PrimaryColor),
                            modifier = Modifier
//                                .padding(top = 20.dp, start = 30.dp, end = 30.dp, bottom = 0.dp)
                                .width(110.dp)
                                .height(30.dp),
                        ) {
                            Text(
                                text = "abc",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row {
                        Text(
                            text = "Enter Stock:",
                            fontSize = 10.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(Color.White),
                            border = BorderStroke(width = 1.dp, color = PrimaryColor),
                            modifier = Modifier
                                .padding(top = 0.dp, start = 30.dp, end = 0.dp, bottom = 0.dp)
                                .width(110.dp)
                                .height(30.dp)
                        ) {
                            Text(
                                text = "xyz",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                    }


                }

                Box(
                    Modifier
                        .height(60.dp)
                        .width(110.dp)
                        .border(1.dp, PrimaryColor, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ClickImage()
                }
            }
        }
    }
}