package com.example.quicktasker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quicktasker.databinding.FragmentProfileBinding
import com.example.quicktasker.viewmodel.TaskViewModel
import kotlin.math.roundToInt

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        taskViewModel.seedIfEmpty()

        taskViewModel.totalCount.observe(viewLifecycleOwner) { binding.totalTasksText.text = it.toString() }
        taskViewModel.doneCount.observe(viewLifecycleOwner) { done ->
            binding.doneTasksText.text = done.toString()
            updateWeeklyProgress()
        }
        taskViewModel.tasks.observe(viewLifecycleOwner) {
            updateWeeklyProgress()
        }
        taskViewModel.streakCount.observe(viewLifecycleOwner) { binding.streakText.text = it.toString() }
    }

    private fun updateWeeklyProgress() {
        val total = taskViewModel.totalCount.value ?: 0
        val done = taskViewModel.doneCount.value ?: 0
        val percent = if (total == 0) 0 else ((done.toFloat() / total.toFloat()) * 100f).roundToInt()

        binding.weeklyProgressBar.progress = percent
        binding.weeklyPercentText.text = "$percent%"
        binding.weeklyHintText.text =
            if (percent >= 80) "Great pace—keep it up!"
            else "You’re making progress—keep the momentum going!"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}