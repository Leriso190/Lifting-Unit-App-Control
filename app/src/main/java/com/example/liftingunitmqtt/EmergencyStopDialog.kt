package com.example.liftingunitmqtt

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources

//a dialog that pops up everytime the emergency button gets pressed
class EmergencyStopDialog(context: Context) : Dialog(context) {

    private var tvInfo: TextView? = null

    fun setup() {
        setContentView(R.layout.dialog_emergency_stop)
        window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                context,
                R.drawable.popup_menu_background
            )
        )
        window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //block all Activities
        setCancelable(false)
        tvInfo = findViewById(R.id.tvInfo)
        tvInfo!!.text =
            "The emergency stop button has been pressed. To resume execution of the application please release the emergency stop button after ensuring that it is safe to do so."
    }

}