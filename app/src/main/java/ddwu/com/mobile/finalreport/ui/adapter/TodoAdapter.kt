package ddwu.com.mobile.finalreport.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.finalreport.R
import ddwu.com.mobile.finalreport.data.entity.TodoItem
import ddwu.com.mobile.finalreport.databinding.ItemTodoBinding

class TodoAdapter : ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(itemDiffCallback) {

    companion object {
        val itemDiffCallback = object: DiffUtil.ItemCallback<TodoItem>() {
            override fun areItemsTheSame( oldItem: TodoItem, newItem: TodoItem ): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame( oldItem: TodoItem, newItem: TodoItem ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TodoViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        val iconRes = when (item.categoryId) {
            1 -> R.drawable.ic_person
            2 -> R.drawable.ic_academic
            3 -> R.drawable.ic_shopping
            else -> R.drawable.ic_launcher_foreground
        }
        holder.binding.tvContent.text = item.content
        holder.binding.ivCategoryIcon.setImageResource(iconRes)
        holder.binding.cbIsDone.isChecked = item.isDone
    }

    var checkBoxClickListener: ((position: Int) -> Unit)? = null

    inner class TodoViewHolder(val binding: ItemTodoBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cbIsDone.setOnClickListener {
                checkBoxClickListener?.invoke(bindingAdapterPosition)
            }
        }
    }
}