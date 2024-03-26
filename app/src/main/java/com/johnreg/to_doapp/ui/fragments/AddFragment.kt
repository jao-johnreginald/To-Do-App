package com.johnreg.to_doapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.viewmodel.ToDoViewModel
import com.johnreg.to_doapp.databinding.FragmentAddBinding
import com.johnreg.to_doapp.ui.sharedviewmodel.SharedViewModel
import com.johnreg.to_doapp.utils.hideKeyboardFrom

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding

    private val mToDoViewModel: ToDoViewModel by viewModels()

    private val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenu()
        setUI()
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> areThereChanges()
                    R.id.menu_add -> {
                        createNewItem()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun areThereChanges(): Boolean {
        return if (binding.etTitle.text.isNotEmpty() || binding.etDescription.text.isNotEmpty()) {
            showDialogAndSaveChanges()
            true
        } else {
            hideKeyboardFrom(requireContext(), binding.root)
            false
        }
    }

    private fun showDialogAndSaveChanges() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Save Changes")
            .setMessage("Do you want to save your changes?")
            .setPositiveButton("Save") { _, _ ->
                createNewItem()
            }
            .setNegativeButton("Don't Save") { _, _ ->
                hideKeyboardFrom(requireContext(), binding.root)
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun createNewItem() = when {
        binding.etTitle.text.isEmpty() -> {
            hideKeyboardFrom(requireContext(), binding.root)
            Snackbar.make(binding.root, "Please add a title.", Snackbar.LENGTH_SHORT).show()
        }
        else -> {
            val newItem = ToDoData(
                title = binding.etTitle.text.toString(),
                description = binding.etDescription.text.toString(),
                priority = mSharedViewModel.parseStringToPriority(binding.spinner.selectedItem.toString())
            )
            mToDoViewModel.createItem(newItem)
            hideKeyboardFrom(requireContext(), binding.root)
            showSnackbarWithDismissButton("Added: ${binding.etTitle.text}")
            // Navigate back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }
    }

    private fun showSnackbarWithDismissButton(text: String) {
        val snackbar = Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG)
        snackbar.setAction("Dismiss") { snackbar.dismiss() }
        snackbar.show()
    }

    private fun setUI() {
        // Set the Spinner color
        binding.spinner.onItemSelectedListener = mSharedViewModel.spinnerListener
        // Intercept the back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.etTitle.text.isNotEmpty() || binding.etDescription.text.isNotEmpty()) {
                showDialogAndSaveChanges()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

}