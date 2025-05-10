package com.example.oysana_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oysana_android.data.model.Course
import com.example.oysana_android.databinding.ItemCourseBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.oysana_android.utils.Constants

class CourseAdapter(
    private val onItemClick: (Course) -> Unit
) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            val BASE_URL = Constants.BASE_URL
            binding.tvCourseTitle.text = course.title
            binding.tvCourseDescription.text = course.description

            Glide.with(binding.imgCourseCover.context)
                .load(BASE_URL + course.img)
                .placeholder(android.R.color.darker_gray)
                .into(binding.imgCourseCover)

            binding.root.setOnClickListener {
                onItemClick(course)
            }
        }
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean =
            oldItem == newItem
    }
}


