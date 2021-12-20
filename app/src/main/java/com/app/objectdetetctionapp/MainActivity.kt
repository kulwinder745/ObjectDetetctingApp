package com.app.objectdetetctionapp


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.objectdetetctionapp.databinding.ActivityMainBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage

import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

import java.io.IOException
import java.util.*
import android.os.Build
import com.github.dhaval2404.imagepicker.ImagePicker



class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSION_REQUESTS = 1
    private val TAG = "MainActivity"
    private var binding: ActivityMainBinding? = null
    private var imageUri: Uri? = null
    var objectDetector: ObjectDetector? = null
    var x = 0
    var speed = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        /** get run time permissions*/
        if (!allPermissionsGranted()) {
            getRuntimePermissions()
        }
        /** click listener*/
        binding?.selectImageButton?.setOnClickListener(this)

        val localModel =
            LocalModel.Builder()
                .setAssetFilePath("custom_models/object_labeler.tflite")

                .build()
        val options =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(3)
                .build()
        objectDetector = ObjectDetection.getClient(options)

    }

    private fun getRuntimePermissions() {
        val allNeededPermissions = ArrayList<String>()
        for (permission in getRequiredPermissions()) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    allNeededPermissions.add(permission)
                }
            }
        }

        if (allNeededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
            )
        }
    }

    private fun getRequiredPermissions(): Array<String?> {
        return try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.isNotEmpty()) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: Exception) {
            arrayOfNulls(0)
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in getRequiredPermissions()) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {

            R.id.select_image_button -> {

                ImagePicker.with(this)
                    .crop(1F, 1F)
                    .maxResultSize(1080, 1080)
                    .compress(1024)
                    .start()
            }
        }
    }



    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri = data?.data!!
                imageUri = uri
                binding?.preview?.setImageURI(imageUri)

                tryReloadAndDetectInImage()


            }
            ImagePicker.RESULT_ERROR -> {

                GlobalFunction.showToast(ImagePicker.getError(data))
            }
            else -> {
                GlobalFunction.showToast("Task Cancelled")

            }
        }


    }

    private fun tryReloadAndDetectInImage() {
        val image: InputImage
        try {

            image = InputImage.fromFilePath(this, imageUri)

            objectDetector?.process(image)
               ?.addOnSuccessListener { detectedObjects ->

                   for (detectedObject in detectedObjects) {
                       val boundingBox = detectedObject.boundingBox
                       val trackingId = detectedObject.trackingId
                       var text = "Unknown"
                       for (label in detectedObject.labels) {
                            text = label.text
                           Log.d(TAG, "tryReloadAndDetectInImage: "+boundingBox)

                       }
                       val drawGraphic = DrawGraphic(this,boundingBox,text)
                       binding?.parentLayout?.addView(drawGraphic)
                   }



                }
                ?.addOnFailureListener { e ->
                    Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
                }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }




}