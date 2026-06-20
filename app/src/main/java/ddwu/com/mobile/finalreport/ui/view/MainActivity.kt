package ddwu.com.mobile.finalreport.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.finalreport.R
import ddwu.com.mobile.finalreport.data.database.TodoDatabase
import ddwu.com.mobile.finalreport.data.entity.TodoItem
import ddwu.com.mobile.finalreport.databinding.ActivityMainBinding
import ddwu.com.mobile.finalreport.ui.adapter.TodoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val todoDao by lazy {
        TodoDatabase.getDatabase(applicationContext).todoDao()
    }

    val adapter = TodoAdapter()

    var showFinishedTodoItems: Boolean = false

    lateinit var allTodoList: List<TodoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // adapter 설정
        adapter.checkBoxClickListener = { position: Int ->
            val item: TodoItem = adapter.currentList[position]
            item.isDone = !item.isDone
            CoroutineScope(Dispatchers.IO).launch {
                delay(300L.milliseconds)
                todoDao.updateTodoItem(item)
                refreshRecyclerView()
            }
        }
        adapter.itemClickListener = { item ->
            val categoryName = when (item.categoryId) {
                1 -> "개인"
                2 -> "학업"
                3 -> "쇼핑"
                else -> "기타"
            }
            val iconRes = when (item.categoryId) {
                1 -> R.drawable.ic_person
                2 -> R.drawable.ic_academic
                3 -> R.drawable.ic_shopping
                else -> R.drawable.ic_launcher_foreground
            }
            val isDoneText = if (item.isDone) "완료" else "미완료"
            var message = "카테고리: $categoryName \n마감일: ${item.dueDate} \n상태: $isDoneText"
            if (!item.memo.isNullOrEmpty()) {
                message += "\n\n[메모]\n${item.memo}"
            }
            val builder = AlertDialog.Builder(this)
                .setIcon(iconRes)
                .setTitle(item.content)
                .setMessage(message)
                .setPositiveButton("확인", null)
            builder.show()
        }
        adapter.editClickListener = { item ->
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("item", item)
            startActivity(intent)
        }
        adapter.deleteClickListener = { item ->
            val builder = AlertDialog.Builder(this).apply {
                setTitle("할 일 삭제")
                setMessage("[${item.content}] 할 일을 삭제하시겠습니까?")
                setPositiveButton("삭제") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        todoDao.deleteTodoItem(item)
                        refreshRecyclerView()
                    }
                }
                setNegativeButton("취소", null)
                setCancelable(true)
            }
            builder.show()
        }

        // layoutManager 설정
        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        // decoration 설정
        val decor = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)

        // RecyclerView 설정
        binding.rvItems.adapter = adapter
        binding.rvItems.layoutManager = layoutManager
        binding.rvItems.addItemDecoration(decor)

        // 아이템 Flow 관찰
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) {
            val listFlow: Flow<List<TodoItem>> = todoDao.getAllTodoItems()
            listFlow.distinctUntilChanged().collect { items ->
                allTodoList = items
                refreshRecyclerView()
            }
        }}
    }

    fun refreshRecyclerView() {
        val filteredList = if (showFinishedTodoItems) {
            allTodoList
        } else {
            allTodoList.filter { !it.isDone }
        }
        adapter.submitList(filteredList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuAdd -> {
                startActivity(Intent(this, AddActivity::class.java))
                true
            }
            R.id.menuVisibility -> {
                item.isChecked = !item.isChecked
                showFinishedTodoItems = item.isChecked
                refreshRecyclerView()
                true
            }
            R.id.menuDeleteDoneItems -> {
                val builder = AlertDialog.Builder(this).apply {
                    setTitle("완료된 할 일 삭제")
                    setMessage("완료된 할 일을 전부 삭제하시겠습니까?")
                    setPositiveButton("삭제") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            todoDao.deleteDoneItems()
                            refreshRecyclerView()
                        }
                    }
                    setNegativeButton("취소", null)
                    setCancelable(true)
                }
                builder.show()
                true
            }
            R.id.menuIntroduction -> {
                val builder = AlertDialog.Builder(this).apply {
                    setTitle("Todo App")
                    setMessage("모바일소프트웨어 02분반 20230774 김민주입니다.\n아이템을 한 번 클릭하면 상세보기, 길게 클릭하면 수정/삭제할 수 있습니다.")
                    setPositiveButton("확인", null)
                    setCancelable(true)
                }
                builder.show()
                true
            }
            R.id.menuExit -> {
                val builder = AlertDialog.Builder(this).apply {
                    setTitle("앱 종료")
                    setMessage("앱을 종료하시겠습니까?")
                    setPositiveButton("종료") { _, _ ->
                        finish()
                    }
                    setNegativeButton("취소", null)
                    setCancelable(true)
                }
                builder.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}