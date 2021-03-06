package ru.fedurovkostya.todolist


import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_task.*
import ru.fedurovkostya.todolist.Other.DBHelper
import ru.fedurovkostya.todolist.Other.*
import java.net.ContentHandler
// Главный класс, разметка которого появляется после заставки.
// Основное меню приложения.
class TaskActivity : AppCompatActivity() {

    lateinit var dbHelper: DBHelper
    lateinit var context:Context

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        dbHelper = DBHelper(this)
        context = this
        rv_task.layoutManager = LinearLayoutManager(this)
        // Код ниже срабатывает при нажатии на кнопку  +  справа внизу;
        fab_task.setOnClickListener({
            // Открывает окно редактирования;
            val dialog = BottomSheetDialog(this)
            // Объявление и иницилизации объектов разметки;
            val view = layoutInflater.inflate(R.layout.activity_dialog_edit,null)
            var et_name = view.findViewById<EditText>(R.id.et_dialog_name)
            var et_description = view.findViewById<EditText>(R.id.et_dialog_description)
            var cb_priority = view.findViewById<CheckBox>(R.id.cb_dialog_priority)
            var iv_neg = view.findViewById<ImageView>(R.id.iv_dialog_neg)
            var tv_pos = view.findViewById<TextView>(R.id.tv_dialog_pos)
            // При нажатии на крестик или на экран, окно редактирования закроется;
            iv_neg.setOnClickListener {
                dialog.dismiss()
            }
            // Сохраненение задачи в базу данных, при нажатии кнопки  сохранить .
            tv_pos.setOnClickListener {
                var task = Task()
                task.name = et_name.text.toString()
                task.description = et_description.text.toString()
                if(cb_priority.isChecked){
                    task.color = this.resources.getColor(R.color.colorAccent)
                }
                else{
                    task.color = this.resources.getColor(R.color.textColorPrimary)
                }
                dbHelper.addTask(task)
                refreshList()
                dialog.dismiss()
            }
            dialog.setContentView(view)
            dialog.show()
        })
    }
    // Функция служит для корректного отображения задач в списке;
    // и срабатывает при их изменении;
    fun refreshList(){
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rv_task)
        rv_task.adapter = TaskAdapter(this,dbHelper.getTasks())
        hideFab()
    }
    // Функция скрывает кнопку добавления задач, если задач больше 5 и показывает
    // логотип, если количество задач равно 0.
    fun hideFab(){
        if(dbHelper.getTasks().size == 0){
            iv_main.visibility = View.VISIBLE
            iv_main.setImageResource(R.drawable.ic_kotyara)
        }
        else{
            iv_main.visibility = View.GONE
        }
        if(dbHelper.getTasks().size > 5){
            fab_task.hide()
        }else if(fab_task.visibility != View.VISIBLE){
            fab_task.show()
        }
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }
    // Код ниже позволяет удалять задачи, свайпая их влево или вправо.
    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            var task = dbHelper.getTasks()[viewHolder.adapterPosition]
            dbHelper.deleteTask(task.id)
            refreshList()
        }
    }
}
