package com.example.quickcart.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.quickcart.databinding.ItemViewProductCategoriesBinding
import com.example.quickcart.models.Category

class AdapterCategory(
    private val categoryList: ArrayList<Category>,
    val onCategoryClicked: (Category) -> Unit
) :
    RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {
    class CategoryViewHolder(val binding: ItemViewProductCategoriesBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemViewProductCategoriesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.apply {
            ivCategoryImage.setImageResource(category.image)
            ivCategoryTitle.text = category.title
        }
        holder.itemView.setOnClickListener {
            onCategoryClicked(category)
        }
    }

}
