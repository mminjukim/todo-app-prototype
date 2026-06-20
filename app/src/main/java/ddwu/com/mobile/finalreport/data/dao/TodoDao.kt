package ddwu.com.mobile.finalreport.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ddwu.com.mobile.finalreport.data.entity.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_item ORDER BY dueDate ASC")
    fun getAllTodoItems(): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItem(todoItem: TodoItem)

    @Update
    suspend fun updateTodoItem(todoItem: TodoItem)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)
}