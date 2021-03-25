package com.dineshworkspace.sensorapp.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.dineshworkspace.sensorapp.AppConstants
import com.dineshworkspace.sensorapp.R
import com.dineshworkspace.sensorapp.helpers.UiHelper
import io.socket.client.Socket
import javax.inject.Inject

open class BaseActivity(@LayoutRes private val layoutId: Int) : AppCompatActivity() {

    lateinit var parentLayout: View

    @Inject
    lateinit var baseSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        parentLayout = findViewById(android.R.id.content)
        initSocketConnection()

        baseSocket.on(AppConstants.ON_SOCKET_CONNECT) {
            showSnackBarWithAutoClosure(getString(R.string.socket_success_msg))
        }

        baseSocket.on(AppConstants.ON_CONNECT_ERROR) {
            showSnackBar(getString(R.string.socket_failed_msg))
        }
    }

    private fun initSocketConnection() {
        if (!baseSocket.isActive) {
            baseSocket.connect()
        }
    }


    protected fun showSnackBar(msg: String?) {
        parentLayout.let {
            UiHelper.showSnackBar(it, msg, getString(R.string.close_caps))
        }
    }

    protected fun showSnackBarWithAutoClosure(msg: String?) {
        parentLayout.let {
            UiHelper.showSnackBarWithAutoClosure(it, msg, getString(R.string.close_caps))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        baseSocket.off()
        baseSocket.close()
    }

}