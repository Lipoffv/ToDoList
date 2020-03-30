package ru.fedurovkostya.todolist


import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_task.*
import ru.fedurovkostya.todolist.Other.Task
// Класс служит адаптером между базой данных, списком и пользователем.
class TaskAdapter(val activity: TaskActivity, var list:MutableList<Task>): RecyclerView.Adapter<TaskAdapter.ViewHolder>(){

    // Функция создает сам список.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(activity).inflate(
                R.layout.task_item,
                parent,
                false
            )
        )
    }
    // Возвращает количество объектов;
    override fun getItemCount(): Int {
        return list.size
    }
    // Этот блок кода сработает при нажатии на задачу или при ее создании;
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setSizeOfTask(activity,holder)
        // Установка значений: имя, цвет checkbox;
        holder.tv_task.text = list[position].name
        if(list[position].color == activity.resources.getColor(R.color.colorAccent)){
            holder.iv_task.setColorFilter(activity.resources.getColor(R.color.colorAccent))
        }
        // При нажатии на checkbox, задача удаляется;
        holder.iv_task.setOnClickListener({
            activity.dbHelper.deleteTask(list[position].id)
            activity.refreshList()
        })
        // При нажатии на задачу, появляется окно для ее редактирования;
        holder.ll_task.setOnClickListener({
            // Объявление и инициализация объектов;
            val dialog = BottomSheetDialog(activity)
            val view = activity.layoutInflater.inflate(R.layout.activity_dialog_edit,null)
            var et_name = view.findViewById<EditText>(R.id.et_dialog_name)
            var et_description = view.findViewById<EditText>(R.id.et_dialog_description)
            var cb_priority = view.findViewById<CheckBox>(R.id.cb_dialog_priority)
            var iv_neg = view.findViewById<ImageView>(R.id.iv_dialog_neg)
            var tv_pos = view.findViewById<TextView>(R.id.tv_dialog_pos)
            // Установка значения;
            et_description.setText(list[position].description)
            et_name.setText(list[position].name)
            cb_priority.isChecked = activity.resources.getColor(R.color.colorAccent) == list[position].color
            tv_pos.text = "Сохранить"
            iv_neg.setImageResource(R.drawable.ic_back)
            // При нажатии на стрелочку назад, окно редактирования закрывается;
            iv_neg.setOnClickListener {
                dialog.dismiss()
            }
            // При нажатии на кнопку Сохранить, данные записываются в базу данных, а затем окно закрывается;
            tv_pos.setOnClickListener {
                var task = list[position]
                task.name = et_name.text.toString()
                task.description = et_description.text.toString()
                if(cb_priority.isChecked){
                    task.color = activity.resources.getColor(R.color.colorAccent)
                }
                else{
                    task.color = activity.resources.getColor(R.color.textColorPrimary)
                }
                activity.dbHelper.updateTask(task)
                activity.refreshList()
                dialog.dismiss()
            }
            dialog.setContentView(view)
            dialog.show()
        })
    }

    // Установка высоты задачи, как 1/6 части экрана;
    fun setSizeOfTask(activity: TaskActivity, holder: ViewHolder){
        var statusBarHeight = 0
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = activity.resources.getDimensionPixelSize(resourceId)
        }
        val displaymetrics = DisplayMetrics()
        (activity as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
        val deviceheight = (displaymetrics.heightPixels-
                activity.ll_top.layoutParams.height-statusBarHeight-70) / 6
        holder.ll_task.getLayoutParams().height = deviceheight
    }

    // Объявление и инициализации объектов в разметке задачи;
    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val tv_task: TextView = v.findViewById(R.id.tv_task)
        val iv_task: ImageView = v.findViewById(R.id.iv_task)
        val ll_task: LinearLayout = v.findViewById(R.id.ll_task)
    }

}
