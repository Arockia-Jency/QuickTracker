package com.example.quicktasker

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quicktasker.databinding.FragmentAddBinding
import com.example.quicktasker.models.TaskPriority
import com.example.quicktasker.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddFragment : Fragment(R.layout.fragment_add) {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by activityViewModels()

    private var selectedDueDateMillis: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddBinding.bind(view)

        binding.dueDateEditText.setOnClickListener { openDatePicker() }
        binding.btnSaveTask.setOnClickListener { onSaveClicked() }
    }

    private fun openDatePicker() {
        val cal = Calendar.getInstance()
        selectedDueDateMillis?.let { cal.timeInMillis = it }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val picked = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                selectedDueDateMillis = picked
                binding.dueDateEditText.setText(formatDate(picked))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun onSaveClicked() {
        val title = binding.titleEditText.text?.toString().orEmpty()
        val description = binding.descriptionEditText.text?.toString().orEmpty()

        if (title.isBlank()) {
            Toast.makeText(requireContext(), "Please enter a task title", Toast.LENGTH_SHORT).show()
            return
        }

        val priority = when (binding.priorityToggleGroup.checkedButtonId) {
            R.id.btnLow -> TaskPriority.LOW
            R.id.btnHigh -> TaskPriority.HIGH
            else -> TaskPriority.MEDIUM
        }

        taskViewModel.addTask(
            title = title,
            description = description,
            dueDateMillis = selectedDueDateMillis,
            priority = priority
        )

        Toast.makeText(requireContext(), "Task saved", Toast.LENGTH_SHORT).show()
        (activity as? MainActivity)?.selectTasksTab()
    }

    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}