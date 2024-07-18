package com.js.sd.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.js.sd.R
import com.js.sd.activities.constants.Tabs
import com.js.sd.cache.CacheManager
import com.js.sd.cache.sqlite.SQLiteManager
import com.js.sd.exceptions.InvalidTimeException
import com.js.sd.exceptions.UncaughtExceptionHandler
import com.js.sd.https.ApiClient
import com.js.sd.model.Student
import com.js.sd.properties.Properties
import com.js.sd.util.Logman
import com.js.sd.util.Timeman
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : SDActivity() {

    private var students = ArrayList<Student>()
    private var isTracking = false

    private lateinit var cacheManager: CacheManager
    private lateinit var uncaughtExceptionHandler: UncaughtExceptionHandler
    private lateinit var thread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setCheckedTab(Tabs.HOME)
        moveTaskToBackgroundOnBack()
        initFileLogger()
        createCacheManager()
        thread = Thread(this::populateStudents)

        // to be used for this thread and all future threads
        uncaughtExceptionHandler = UncaughtExceptionHandler(this)
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler)

        Logman.logInfoMessage("Starting main activity...")
        thread.start()
    }

    private fun initFileLogger() {
        val directoryForLogs = getExternalFilesDir(null)
        if (directoryForLogs != null) {
            System.setProperty("tinylog.directory", directoryForLogs.absolutePath)

            Logman.logInfoMessage("Process started at {}", Timeman.getCurrentTimestamp())
            Logman.logInfoMessage("Logs can be found in \"{}\"", directoryForLogs)
        } else {
            Logman.logErrorMessage("Null directory for logs.")
        }
    }

    private fun createCacheManager() {
        val sqlManager = SQLiteManager(applicationContext)
        cacheManager = CacheManager(sqlManager, students)
    }

    @Throws(InvalidTimeException::class)
    private fun startCacheIntervals() {
        cacheManager.startCachingInterval(5)
        cacheManager.startClearingInterval(20)
    }

    private fun stopCacheIntervals() {
        cacheManager.stopCachingInterval()
        cacheManager.stopClearingInterval()
    }

    private fun populateStudents() {
        Logman.logInfoMessage("Populating students. Current thread name: {}", Thread.currentThread().name)
        val apiService = ApiClient.createService(Properties.USERNAME, Properties.PASSWORD)
        val call = apiService.getStudents()
        call.enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful && response.body() != null) {
                    Logman.logInfoMessage("Fetch successful.")
                    students.addAll(response.body()!!)
                    createTable()
                } else {
                    Logman.logErrorMessage("Fetch unsuccessful.")
                    Toast.makeText(this@MainActivity, "Response not successful", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Student>>, throwable: Throwable) {
                throwable.message?.let { Logman.logErrorMessage(it) }
                Toast.makeText(this@MainActivity, "Request failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun createTable() {
        val table = findViewById<TableLayout>(R.id.table)
        val rowParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        val iconParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        val elementParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6f)

        val size = students.size
        for (i in 0 until size) {
            val student = students[i]
            val row = TableRow(this)

            // style row
            setRowAttributes(row, i)

            // add views to row (icon, name and student ID)
            addUserIconToRow(row, iconParams)
            addTextViewToRow(student.name, row, elementParams, View.TEXT_ALIGNMENT_TEXT_START)
            addTextViewToRow(
                student.studentId.toString(),
                row,
                elementParams,
                View.TEXT_ALIGNMENT_TEXT_END
            )

            // allow user to click each row to see more details
             addOnClickListenerToRow(row, student)

            table.addView(row, rowParams)
        }
    }

    private fun addOnClickListenerToRow(row: TableRow, student: Student) {
        row.setOnClickListener {
            val intent = Intent(
                this@MainActivity,
                DetailsActivity::class.java
            )
            // pack student for details page
            packStudent(intent, student)

            // pack all students for future requests
            packAllStudents(intent)

            startActivity(intent)
        }
    }

    private fun packStudent(intent: Intent, student: Student) {
        intent.putExtra("name", student.name)
        intent.putExtra("id", student.studentId)
        intent.putExtra("address", student.address)
        intent.putExtra("latitude", student.latitude)
        intent.putExtra("longitude", student.longitude)
        intent.putExtra("phone", student.phone)
        intent.putExtra("image", student.image)
    }

    private fun packAllStudents(intent: Intent) {
        val n           = students.size
        val names       = arrayOfNulls<String>(n)
        val ids         = IntArray(n)
        val addresses   = arrayOfNulls<String>(n)
        val latitudes   = DoubleArray(n)
        val longitudes  = DoubleArray(n)
        val phones      = arrayOfNulls<String>(n)
        val images      = arrayOfNulls<String>(n)

        for (i in 0 until n) {
            val student     = students[i]
            names[i]        = student.name
            ids[i]          = student.studentId
            addresses[i]    = student.address
            latitudes[i]    = student.latitude
            longitudes[i]   = student.longitude
            phones[i]       = student.phone
            images[i]       = student.image
        }
        intent.putExtra("names", names)
        intent.putExtra("ids", ids)
        intent.putExtra("addresses", addresses)
        intent.putExtra("latitudes", latitudes)
        intent.putExtra("longitudes", longitudes)
        intent.putExtra("phones", phones)
        intent.putExtra("images", images)
    }

    private fun clearTable() {
        val table = findViewById<TableLayout>(R.id.table)
        table.removeAllViews()
    }

    private fun addUserIconToRow(row: TableRow, iconParams: TableRow.LayoutParams) {
        val userIcon = ImageView(this)
        userIcon.setImageResource(R.drawable.user_icon)
        val userIconPaddingRight = resources.getDimensionPixelSize(R.dimen.icon_padding_right)
        userIcon.setPadding(0, 0, userIconPaddingRight, 0)
        userIcon.scaleType = ImageView.ScaleType.FIT_START
        row.addView(userIcon, iconParams)
    }

    private fun setRowAttributes(row: TableRow, i: Int) {
        val rowPaddingHorizontal = resources.getDimensionPixelSize(R.dimen.row_padding_horizontal)
        val rowPaddingVertical = resources.getDimensionPixelSize(R.dimen.row_padding_vertical)
        row.setPadding(
            rowPaddingHorizontal,
            rowPaddingVertical,
            rowPaddingHorizontal,
            rowPaddingVertical
        )
        row.id = i

        // give every other row an accent
        if (i % 2 == 0) {
            row.setBackgroundColor(resources.getColor(R.color.colorAccentRow, null))
        }
    }

    private fun addTextViewToRow(
        text: String,
        row: TableRow,
        elementParams: TableRow.LayoutParams,
        alignment: Int
    ) {
        // add name text view
        val name = TextView(this)
        name.text = text
        name.textAlignment = alignment
        row.addView(name, elementParams)
    }

    fun handleTrackButton(view: View) {
        val button = findViewById<Button>(R.id.track_button)
        if (!isTracking) {
            try {
                startCacheIntervals()
                button.setText(R.string.stop_tracking)
                isTracking = true
            } catch (e: InvalidTimeException) {
                e.logErrorMessage()
            }
        } else {
            stopCacheIntervals()
            button.setText(R.string.start_tracking)
            isTracking = false
        }
    }

    @Throws(InterruptedException::class)
    fun handleRefresh(view: View) {
        // wait for other thread to die before starting a new one
        thread.join()

        // clear students and table if list is not empty
        if (students.isNotEmpty()) {
            students.clear()
            clearTable()
        }

        thread = Thread { this.populateStudents() }
        thread.uncaughtExceptionHandler = uncaughtExceptionHandler
        thread.start()
    }

    override fun handleMapButton(item: MenuItem) {
        val intent = Intent(this, MapActivity::class.java)
        packAllStudents(intent)
        startActivity(intent)
    }

    override fun handleWebButton(item: MenuItem) {
        val intent = Intent(this, WebActivity::class.java)
        packAllStudents(intent)
        startActivity(intent)
    }
}