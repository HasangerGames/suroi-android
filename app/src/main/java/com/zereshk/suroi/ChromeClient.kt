package com.zereshk.suroi

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class ChromeClient(private val context: Context) : WebChromeClient() {
    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult
    ): Boolean {
        showCustomDialog(message, DialogType.ALERT, result)
        return true
    }
    override fun onJsBeforeUnload(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult
    ): Boolean {
        showCustomDialog(message, DialogType.UNLOAD, result)
        return true
    }
    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult
    ): Boolean {
        showCustomDialog(message, DialogType.CONFIRM, result)
        return true
    }
    override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult
    ): Boolean {
        showCustomDialog(message, DialogType.PROMPT, result, defaultValue)
        return true
    }
    private fun showCustomDialog(
        message: String?,
        dialogType: DialogType,
        result: Any,
        defaultValue: String? = null
    ) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val dialogInput = dialogView.findViewById<EditText>(R.id.dialog_input)
        val dialogCancelButton = dialogView.findViewById<Button>(R.id.dialog_cancel)
        val dialogOkButton = dialogView.findViewById<Button>(R.id.dialog_ok)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder.setCancelable(false)
        val dialog = alertDialogBuilder.create()
        dialogMessage.text = message
        when (dialogType) {
            DialogType.ALERT -> {
                dialogTitle.text = context.getString(R.string.alert)
                dialogCancelButton.visibility = View.GONE
                dialogInput.visibility = View.GONE
                dialogOkButton.setOnClickListener {
                    (result as JsResult).confirm()
                    dialog.dismiss()
                }
            }
            DialogType.CONFIRM -> {
                dialogTitle.text = context.getString(R.string.confirm)
                dialogCancelButton.visibility = View.VISIBLE
                dialogInput.visibility = View.GONE
                dialogOkButton.setOnClickListener {
                    (result as JsResult).confirm()
                    dialog.dismiss()
                }
                dialogCancelButton.setOnClickListener {
                    (result as JsResult).cancel()
                    dialog.dismiss()
                }
            }
            DialogType.UNLOAD -> {
                dialogTitle.text = context.getString(R.string.unload)
                dialogCancelButton.visibility = View.VISIBLE
                dialogInput.visibility = View.GONE
                dialogOkButton.setOnClickListener {
                    (result as JsResult).confirm()
                    dialog.dismiss()
                }
                dialogCancelButton.setOnClickListener {
                    (result as JsResult).cancel()
                    dialog.dismiss()
                }
            }
            DialogType.PROMPT -> {
                dialogTitle.text = context.getString(R.string.prompt)
                dialogCancelButton.visibility = View.VISIBLE
                dialogInput.visibility = View.VISIBLE
                dialogInput.setText(defaultValue)
                dialogOkButton.setOnClickListener {
                    (result as JsPromptResult).confirm(dialogInput.text.toString())
                    dialog.dismiss()
                }
                dialogCancelButton.setOnClickListener {
                    (result as JsPromptResult).cancel()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }
    private enum class DialogType {
        ALERT, UNLOAD, CONFIRM, PROMPT
    }
}