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
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.johnreg.to_doapp.R
import com.johnreg.to_doapp.data.models.ToDoData
import com.johnreg.to_doapp.data.viewmodel.ToDoViewModel
import com.johnreg.to_doapp.databinding.FragmentUpdateBinding
import com.johnreg.to_doapp.ui.adapter.SpinnerAdapter
import com.johnreg.to_doapp.utils.getPositionFrom
import com.johnreg.to_doapp.utils.getPriorityFrom
import com.johnreg.to_doapp.utils.hideKeyboard
import com.johnreg.to_doapp.utils.showSnackbar
import com.johnreg.to_doapp.utils.showSnackbarAndDismiss

class UpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBinding

    private val args: UpdateFragmentArgs by navArgs()

    private val mToDoViewModel: ToDoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateBinding.inflate(inflater, container, false)
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
                // Add menu items here
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        if (hasChanges()) {
                            showDialogAndSaveChanges()
                            true
                        } else {
                            hideKeyboard()
                            false
                        }
                    }

                    R.id.menu_save -> {
                        updateItem()
                        true
                    }

                    R.id.menu_delete -> {
                        showDialogAndDeleteItem()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showDialogAndSaveChanges() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.save_changes_title))
            .setMessage(getString(R.string.save_changes_message))
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                updateItem()
            }
            .setNegativeButton(getString(R.string.do_not_save)) { _, _ ->
                // Hide keyboard, show Snackbar, navigate back
                hideKeyboard()
                showSnackbarAndDismiss(getString(R.string.changes_not_saved))
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNeutralButton(getString(R.string.cancel), null)
            .show()
    }

    private fun updateItem() {
        if (binding.etTitle.text.isEmpty()) {
            hideKeyboard()
            showSnackbar(getString(R.string.please_add_a_title))
        } else {
            val updatedItem = ToDoData(
                id = args.currentItem.id,
                title = binding.etTitle.text.toString(),
                description = binding.etDescription.text.toString(),
                priority = getPriorityFrom(binding.spinner.selectedItem.toString())
            )

            mToDoViewModel.updateItem(updatedItem)
            // Hide keyboard, show Snackbar, navigate back
            hideKeyboard()
            showSnackbarAndDismiss("Updated: ${updatedItem.title}")
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
    }

    private fun showDialogAndDeleteItem() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete [ ${args.currentItem.title} ]?")
            .setMessage("[ ${args.currentItem.title} ] will be deleted. Are you sure?")
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                mToDoViewModel.deleteItem(args.currentItem)
                // Hide keyboard, show Snackbar, navigate back
                hideKeyboard()
                showSnackbarAndDismiss("Deleted: ${args.currentItem.title}")
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun setUI() {
        // Set the texts
        binding.etTitle.setText(args.currentItem.title)
        binding.etDescription.setText(args.currentItem.description)

        // Set the spinner adapter and selection
        binding.spinner.adapter = SpinnerAdapter(
            requireContext(), resources.getStringArray(R.array.priorities)
        )

        binding.spinner.setSelection(getPositionFrom(args.currentItem.priority))

        // Intercept the back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (hasChanges()) {
                showDialogAndSaveChanges()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun hasChanges(): Boolean {
        return args.currentItem.priority != getPriorityFrom(binding.spinner.selectedItem.toString())
                || args.currentItem.title != binding.etTitle.text.toString()
                || args.currentItem.description != binding.etDescription.text.toString()
    }

}