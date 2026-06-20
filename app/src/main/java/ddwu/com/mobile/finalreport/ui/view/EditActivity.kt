package ddwu.com.mobile.finalreport.ui.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.finalreport.R
import ddwu.com.mobile.finalreport.data.database.TodoDatabase
import ddwu.com.mobile.finalreport.data.entity.TodoItem
import ddwu.com.mobile.finalreport.databinding.ActivityEditBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {

    val TAG = "EditActivity"

    val binding by lazy {
        ActivityEditBinding.inflate(layoutInflater)
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

        val item: TodoItem = intent.getSerializableExtra("item") as TodoItem

        var currentSelectedDateStr = ""
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        item.let { item ->
            // 이전 정보로 세팅
            binding.etContent.setText(item.content)
            binding.etMemo.setText(item.memo)
            when (item.categoryId) {
                1 -> binding.rbPersonal.isChecked = true
                2 -> binding.rbAcademic.isChecked = true
                3 -> binding.rbShopping.isChecked = true
                else -> item.categoryId = -1
            }
            currentSelectedDateStr = item.dueDate.toString()
            val date = dateFormat.parse(currentSelectedDateStr)
            if (date != null) {
                binding.calendarView.date = date.time
            }
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            currentSelectedDateStr = dateFormat.format(calendar.time)
        }

        // 저장 버튼
        binding.btnUpdate.setOnClickListener {
            item.content = binding.etContent.text.toString()
            item.memo = binding.etMemo.text.toString()
            item.dueDate = currentSelectedDateStr
            item.categoryId = when (binding.rgCategory.checkedRadioButtonId) {
                binding.rbPersonal.id -> 1
                binding.rbAcademic.id -> 2
                binding.rbShopping.id -> 3
                else -> 4
            }
            CoroutineScope(Dispatchers.IO).launch {
                todoDao.updateTodoItem(item)
            }
            finish()
        }

        // 취소 버튼
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}