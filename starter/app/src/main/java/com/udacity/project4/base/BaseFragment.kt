package com.udacity.project4.base

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

/**
 * Base Fragment to observe on the common LiveData objects
 */
abstract class BaseFragment : Fragment() {
    /**
     * Every fragment has to have an instance of a view model that extends from the BaseViewModel
     */
    abstract val _viewModel: BaseViewModel

    override fun onStart() {
        super.onStart()
        _viewModel.showErrorMessage.observe(this, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        _viewModel.showToast.observe(this, Observer {
//            Toast.makeText(requireContext().applicationContext, it, Toast.LENGTH_LONG).show()
            val toast: Toast = Toast.makeText(context, it, Toast.LENGTH_LONG)
            val textView = TextView(context)
            textView.setBackgroundColor(Color.DKGRAY)
            textView.setTextColor(Color.WHITE)
            textView.textSize = 15f
            val typeface: Typeface = Typeface.create("serif", Typeface.BOLD)
            textView.typeface = typeface
            textView.setPadding(10, 10, 10, 10)
            textView.text = it

            toast.view = textView
            toast.show()

        })
        _viewModel.showSnackBar.observe(this, Observer {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        })
        _viewModel.showSnackBarInt.observe(this, Observer {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        })

        _viewModel.navigationCommand.observe(this, Observer { command ->
            when (command) {
                is NavigationCommand.To -> findNavController().navigate(command.directions)
                is NavigationCommand.Back -> findNavController().popBackStack()
                is NavigationCommand.BackTo -> findNavController().popBackStack(
                    command.destinationId,
                    false
                )
            }
        })
    }
}