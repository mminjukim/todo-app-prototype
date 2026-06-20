package ddwu.com.mobile.finalreport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "todo_item")
data class TodoItem(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    var content: String,

    var dueDate: String?,

    var memo: String?,

    var isDone: Boolean = false,

    var categoryId: Int?,

): Serializable