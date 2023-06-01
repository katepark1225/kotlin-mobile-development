package com.example.myapplication
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.math.BigInteger
import java.security.MessageDigest

class MainParent : AppCompatActivity() {

    private var data: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 풀스크린 Dialog Layout을 통해 효율적으로 데이터를 표시함
        val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.show()
    }

     // Sqlite 응용 예시
    private fun readData() {
        val dbHelper = FeedReaderDbHelper(this)
        val db = dbHelper.readableDatabase
        val projection = arrayOf(BaseColumns._ID, FeedReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)
        val cursor = db.query(
            FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,          // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,
            null,
            null
        )
        val itemIds = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                itemIds.add(itemId)
            }
        }
        cursor.close()
        if (itemIds.size == 0) {
            // 데이터 없음
        }
    }

    private fun deleteData() {
        val dbHelper = FeedReaderDbHelper(this)
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM ${FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME}");
    }

    private fun findData() {
        val dbHelper = FeedReaderDbHelper(this)
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE
        )
        val selection =
            "${FeedReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} = ?"
        val selectionArgs = arrayOf(data.sha256())
        val cursor = db.query(
            FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,
            null,
            null
        )
        val itemIds = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                itemIds.add(itemId)
            }
        }
        cursor.close()
    }

    private fun insertData() {
        val dbHelper = FeedReaderDbHelper(this)
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FeedReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, data.sha256())
        }
        val newRowId = db?.insert(FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
        if (newRowId?.toInt() == -1) {
            // 호환되지 않는 기기
        }
    }

    // Hash-256으로 텍스트 암호화
    private fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
}
