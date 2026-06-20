package ddwu.com.mobile.finalreport.ui.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.finalreport.R
import ddwu.com.mobile.finalreport.data.database.TodoDatabase
import ddwu.com.mobile.finalreport.data.entity.TodoItem
import ddwu.com.mobile.finalreport.databinding.ActivityAddBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddActivity : AppCompatActivity() {

    val TAG = "AddActivity"

    val binding by lazy {
        ActivityAddBinding.inflate(layoutInflater)
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

        // 캘린더 날짜 선택
        val calendar = Calendar.getInstance()
        var selectedDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
        }

        // 저장 버튼 클릭
        binding.btnSave.setOnClickListener {
            val content = binding.etContent.text.toString().trim()
            var memo: String? = binding.etMemo.text.toString().trim()
            if (memo?.isEmpty() == true) {
                memo = null
            }

            // 내용 비었으면 토스트
            if (content.isEmpty()) {
                Toast.makeText(this, "할 일을 입력해주세요.", Toast.LENGTH_SHORT).show()

            } else if (binding.rgCategory.checkedRadioButtonId == -1) {
                Toast.makeText(this, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()

            } else {
                // 카테고리 ID 지정
                val categoryId = when (binding.rgCategory.checkedRadioButtonId) {
                    R.id.rbPersonal -> 1
                    R.id.rbAcademic -> 2
                    R.id.rbShopping -> 3
                    else -> null
                }
                // TodoItem 객체 생성
                val newTodo = TodoItem(
                    id = 0,
                    content = content,
                    dueDate = selectedDate,
                    memo = memo,
                    isDone = false,
                    categoryId = categoryId
                )
                CoroutineScope(Dispatchers.IO).launch {
                    todoDao.insertTodoItem(newTodo)
                }
                finish()
            }
        }

        // 취소 버튼 클릭
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}