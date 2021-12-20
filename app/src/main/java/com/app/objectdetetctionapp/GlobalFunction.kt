package com.app.objectdetetctionapp

object GlobalFunction {

    fun showToast(message: String) {
        android.widget.Toast.makeText(MyApplication.getAppInstance(), message, android.widget.Toast.LENGTH_SHORT).show()
    }
}