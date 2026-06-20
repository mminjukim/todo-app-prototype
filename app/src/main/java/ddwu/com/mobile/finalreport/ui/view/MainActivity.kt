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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val todoDao by lazy {
        TodoDatabase.getDatabase(applicationContext).todoDao()
    }

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
        val adapter = TodoAdapter()
        adapter.checkBoxClickListener = { position: Int ->
            val item: TodoItem = adapter.currentList[position]
            item.isDone = !item.isDone
            CoroutineScope(Dispatchers.IO).launch {
                todoDao.updateTodoItem(item)
            }
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
            val listFlow = todoDao.getAllTodoItems()
            listFlow.distinctUntilChanged().collect { items ->
                adapter.submitList(items)
            }
        }}
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
            R.id.menuIntroduction -> {
                val builder = AlertDialog.Builder(this).apply {
                    setTitle("Todo App")
                    setMessage("모바일소프트웨어 02분반 20230774 김민주입니다.")
                    setPositiveButton("확인", null)
                    setCancelable(true)
                }
                builder.show()
                true
            }
            R.id.menuExit -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}