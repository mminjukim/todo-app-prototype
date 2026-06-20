package ddwu.com.mobile.finalreport.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.finalreport.R
import ddwu.com.mobile.finalreport.data.entity.TodoItem
import ddwu.com.mobile.finalreport.databinding.ItemTodoBinding

class TodoAdapter : ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(itemDiffCallback) {

    companion object {
        val itemDiffCallback = object: DiffUtil.ItemCallback<TodoItem>() {
            override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
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
    var itemClickListener: ((item: TodoItem) -> Unit)? = null
    var editClickListener: ((item: TodoItem) -> Unit)? = null
    var deleteClickListener: ((item: TodoItem) -> Unit)? = null

    inner class TodoViewHolder(val binding: ItemTodoBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cbIsDone.setOnClickListener {
                checkBoxClickListener?.invoke(bindingAdapterPosition)
            }
            binding.root.setOnClickListener {
                itemClickListener?.invoke(getItem(bindingAdapterPosition))
            }
            binding.root.setOnLongClickListener {
                val item = getItem(bindingAdapterPosition)
                // 팝업 메뉴 생성
                val popupMenu = PopupMenu(binding.root.context, binding.root)
                popupMenu.menuInflater.inflate(R.menu.todo_popup, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit -> {
                            editClickListener?.invoke(item)
                            true
                        }
                        R.id.action_delete -> {
                            deleteClickListener?.invoke(item)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
                true
            }
        }
    }
}