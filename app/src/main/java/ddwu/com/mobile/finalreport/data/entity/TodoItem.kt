package ddwu.com.mobile.finalreport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_item")
data class TodoItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    var content: String,

    var dueDate: String?,

    var memo: String?,

    var isDone: Boolean = false,

    var categoryId: Int?,
)