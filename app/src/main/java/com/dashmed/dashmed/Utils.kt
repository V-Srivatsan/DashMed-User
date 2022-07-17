package com.dashmed.dashmed

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Patterns
import android.view.LayoutInflater
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class Utils {
    companion object {

        enum class VALIDATION_TYPE {
            TEXT,
            PHONE,
            PASSWORD,
            NUMBER,
            EMAIL
        }

        fun validateField(input: TextInputLayout, type: VALIDATION_TYPE, field_name: String? = null): Boolean {
            when (type) {

                VALIDATION_TYPE.TEXT -> {
                    if (input.editText?.text.isNullOrEmpty()) {
                        input.error = "$field_name cannot be empty!"
                        return false
                    }
                    input.isErrorEnabled = false
                    return true
                }

                VALIDATION_TYPE.PHONE -> {
                    if (!Patterns.PHONE.matcher(input.editText?.text.toString()).matches() || input.editText?.text.toString().length < 14) {
                        input.error = "Please provide a valid phone number!"
                        return false
                    }
                    input.isErrorEnabled = false
                    return true
                }

                VALIDATION_TYPE.PASSWORD -> {
                    val regex = Regex(".{4,}[0-9]{1,}")

                    if (input.editText?.text.isNullOrEmpty()) {
                        input.error = "Password cannot be empty!"
                        return false
                    } else if (input.editText?.text.toString().length < 5) {
                        input.error = "Password is too short!"
                        return false
                    } else if (! input.editText?.text.toString().matches(regex)) {
                        input.error = "Password should have at least 5 characters with a digits as suffix!"
                        return false
                    }
                    input.isErrorEnabled = false
                    return true
                }

                VALIDATION_TYPE.NUMBER -> {
                    val value = input.editText?.text.toString().toIntOrNull()
                    if (value == null) {
                        input.error = "$field_name cannot be empty!"
                        return false
                    } else if (value < 1) {
                        input.error = "$field_name cannot be less than 1!"
                        return false
                    }
                    input.isErrorEnabled = false
                    return true
                }

                VALIDATION_TYPE.EMAIL -> {
                    if (input.editText?.text.toString().isEmpty()) {
                        input.error = "Email cannot be empty!"
                        return false
                    } else if (! Patterns.EMAIL_ADDRESS.matcher(input.editText?.text.toString()).matches()) {
                        input.error = "Please provide a valid email address!"
                        return false
                    }
                    input.isErrorEnabled = false
                    return true
                }

            }
        }

        fun showDialog (prompt: Int, inflater: LayoutInflater, context: Context): AlertDialog {
            val view = inflater.inflate(prompt, null)
            val dialog = AlertDialog.Builder(context).setView(view).create()
            dialog.setCancelable(false)
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            return dialog
        }

        fun showSnackbar (view: CoordinatorLayout, message: String?) {
            Snackbar.make(view, message ?: view.context.getString(R.string.network_error), Snackbar.LENGTH_SHORT).show()
        }

        fun showBottomSheet (layout: Int, inflater: LayoutInflater, context: Context): BottomSheetDialog {
            val view = inflater.inflate(layout, null)
            val dialog = BottomSheetDialog(context, R.style.SheetDialog)
            dialog.setContentView(view)
            dialog.show()
            return dialog
        }

        fun clearFragmentStack (fragmentManager: FragmentManager) {
            for (i in 0 until fragmentManager.backStackEntryCount) fragmentManager.popBackStack()
        }

        fun getUID(activity: Activity): String? {
            return activity.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("UID", null)
        }
    }
}