package com.example.to_dooo.Utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.to_dooo.Model.ToDoModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, NAME, null, VERSION) {

    companion object {
        private const val VERSION = 1
        private const val NAME = "toDoListDatabase"
        private const val TODO_TABLE = "todo"
        private const val ID = "id"
        private const val TASK = "task"
        private const val STATUS = "status"
        private const val CREATE_TODO_TABLE =
            "CREATE TABLE $TODO_TABLE($ID INTEGER PRIMARY KEY AUTOINCREMENT, $TASK TEXT, $STATUS INTEGER)"
    }

    private var db: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TODO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TODO_TABLE")
        onCreate(db)
    }

    fun openDatabase() {
        db = this.writableDatabase
    }

    fun insertTask(task: ToDoModel) {
        val cv = ContentValues()
        cv.put(TASK, task.task)
        cv.put(STATUS, 0)
        db?.insert(TODO_TABLE, null, cv)
    }


    @SuppressLint("Range")
    fun getAllTasks(): List<ToDoModel> {
        val taskList: MutableList<ToDoModel> = ArrayList()
        var cur: Cursor? = null
        db?.beginTransaction()
        try {
            cur = db?.query(TODO_TABLE, null, null, null, null, null, null, null)
            cur?.let {
                if (it.moveToFirst()) {
                    do {
                        val task = ToDoModel()
                        task.id = it.getInt(it.getColumnIndex(ID))
                        task.task = it.getString(it.getColumnIndex(TASK))
                        task.status = it.getInt(it.getColumnIndex(STATUS))
                        taskList.add(task)
                    } while (it.moveToNext())
                }
            }
        } finally {
            db?.endTransaction()
            cur?.close()
        }
        return taskList
    }

    fun updateStatus(id: Int, status: Int) {
        val cv = ContentValues()
        cv.put(STATUS, status)
        db?.update(TODO_TABLE, cv, "$ID = ?", arrayOf(id.toString()))
    }

    fun updateTask(id: Int, task: String) {
        val cv = ContentValues()
        cv.put(TASK, task)
        db?.update(TODO_TABLE, cv, "$ID = ?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Int) {
        db?.delete(TODO_TABLE, "$ID = ?", arrayOf(id.toString()))
    }
}
