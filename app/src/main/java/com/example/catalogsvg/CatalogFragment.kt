package com.example.catalogsvg

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.catalogsvg.databinding.FragmentCatalogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.system.exitProcess
import android.Manifest
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.CaseMap.Fold
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.concurrent.TimeUnit
import android.graphics.pdf.PdfDocument

@SuppressLint("NotifyDataSetChanged")
class CatalogFragment : Fragment(), FolderClick {
    val apiKey = "AIzaSyA-4vWcdoOK_ZKXeLA2eR0Cls1ELoKaUss"
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: CatalogViewModel
    lateinit var dao: CatalogDao
    var isCache = false
    var listOfIdDocVsd = mutableMapOf<String, String>()
    lateinit var adapter: FolderAdapter
    var listOfKTP = mutableListOf<String>()
    var api = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        val view = binding.root
        isCache = getBooleanFromPreferences(requireContext())
        viewModel = ViewModelProvider(this).get(CatalogViewModel::class.java)
        dao = CatalogDatabase.getInstance(requireContext()).dao
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1)
        }
        arguments.let { bundle ->
            if (bundle != null) {
               api = bundle.getBoolean("haveApiKey")
            }
        }
        binding.imgBtnSearch.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("haveApiKey" , api)
            }
            findNavController().navigate(R.id.action_catalogFragment_to_mapsFragment, bundle)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1002)
        }
        adapter = FolderAdapter(listOf("1 Карточки тушения пожаров", "2 Планы тушения пожаров"), this)
        binding.rvFolders.adapter = adapter
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val listBack = adapter.getAll()
            for(item in listBack) {
                if(item.contains("doc") || item.contains("docx") || item.contains("pdf")|| item.contains("vsd")) {
                    Log.e("fvcvcvc", listBack.toString())
                    binding.view1.visibility = View.VISIBLE
                    binding.view2.visibility = View.VISIBLE
                    adapter = FolderAdapter(listOf("1 Карточки тушения пожаров", "2 Планы тушения пожаров"), this@CatalogFragment)
                    binding.rvFolders.adapter = adapter
                }
                if(item.contains("КТП №")) {
                    binding.view1.visibility = View.VISIBLE
                    binding.view2.visibility = View.VISIBLE
                    adapter = FolderAdapter(
                        listOf("1 КТП на объекты, организации", "2 КТП на сельские населенные пункты", "3 КТП на садоводческие товарищества"),
                        this@CatalogFragment
                    )
                    binding.rvFolders.adapter = adapter
                }
                if(item.contains("КТП на")) {
                    binding.view1.visibility = View.VISIBLE
                    binding.view2.visibility = View.VISIBLE
                    adapter = FolderAdapter(listOf("1 Карточки тушения пожаров", "2 Планы тушения пожаров"), this@CatalogFragment)
                    binding.rvFolders.adapter = adapter
                }
                if(item.contains("ПТП №")) {
                    binding.view1.visibility = View.VISIBLE
                    binding.view2.visibility = View.VISIBLE
                    adapter = FolderAdapter(listOf("1 Карточки тушения пожаров", "2 Планы тушения пожаров"), this@CatalogFragment)
                    binding.rvFolders.adapter = adapter
                }
                if(item.contains("Детский сад Колосок") || item.contains("МОУ ДОД Дом детского творчества")) {
                    binding.view1.visibility = View.VISIBLE
                    binding.view2.visibility = View.VISIBLE
                    adapter = FolderAdapter(listOf("1 Карточки тушения пожаров", "2 Планы тушения пожаров"), this@CatalogFragment)
                    binding.rvFolders.adapter = adapter
                }
                if(item.startsWith("с. Ягуново")) {
                    binding.view1.visibility = View.VISIBLE
                    binding.view2.visibility = View.VISIBLE
                    adapter = FolderAdapter(listOf("1 Карточки тушения пожаров", "2 Планы тушения пожаров"), this@CatalogFragment)
                    binding.rvFolders.adapter = adapter
                }
            }
        }
        val currentLogin = checkUserLogin(requireContext())
        Log.e("qwq", currentLogin.toString())
        if(currentLogin == "2psch-1pso")
            getInfoFromServer("https://www.googleapis.com/drive/v3/files?q=%271nJKcnJVQYCoaOfsr4wghKvk0W3MMxi8L%27+in+parents&fields=files(id,name,mimeType)&key=AIzaSyA-4vWcdoOK_ZKXeLA2eR0Cls1ELoKaUss")
        if(currentLogin == "5psch-1pso")
            getInfoFromServer("https://www.googleapis.com/drive/v3/files?q=%271iV_MFbNzxrR3h3r3Iwp5-rpAFj_nCt1C%27+in+parents&fields=files(id,name,mimeType)&key=AIzaSyA-4vWcdoOK_ZKXeLA2eR0Cls1ELoKaUss")
        binding.imgBtnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_catalogFragment_to_settingsFragment)
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    filterFiles(query)
                } else {
                    showAllFiles()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        return view
    }
    private fun checkUserLogin(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", "")
    }

    private fun filterFiles(query: String) {
        viewModel.viewModelScope.launch {
            binding.view1.visibility = View.GONE
            binding.view2.visibility = View.GONE
            val allFile = viewModel.getAll(dao)
            val listNames = mutableListOf<String>()
            for(file in allFile) {
                listNames.add(file.name)
            }
            val filteredList = if (query.isNotEmpty()) {
                listNames.filter { it.contains(query, ignoreCase = true) }
            } else {
                listNames
            }
            adapter = FolderAdapter(filteredList, this@CatalogFragment)
            binding.rvFolders.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun showAllFiles() {
        binding.view1.visibility = View.VISIBLE
        binding.view2.visibility = View.VISIBLE
        adapter = FolderAdapter(listOf("1 Карточки тушения пожаров", "2 Планы тушения пожаров"), this@CatalogFragment)
        binding.rvFolders.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun clickOnFolder(name: String) {
        viewModel.viewModelScope.launch {
            val sharedPrefManager = SharedPrefManager(requireContext())

            val list : List<Catalog> = viewModel.getAll(dao)
            val names = list.map { it.name }
            when {
                name.contains("1 Карточки тушения пожаров") -> {
                    adapter = FolderAdapter(
                        listOf("1 КТП на объекты, организации", "2 КТП на сельские населенные пункты", "3 КТП на садоводческие товарищества"),
                        this@CatalogFragment
                    )
                    binding.rvFolders.adapter = adapter
                }
                name.contains("2 Планы тушения пожаров") -> {
                    binding.view1.visibility = View.GONE
                    binding.view2.visibility = View.GONE
                    val filteredNames = names.filter { it.contains("ПТП") }
                    adapter = FolderAdapter(filteredNames, this@CatalogFragment)
                    binding.rvFolders.adapter = adapter
                }
                name.contains("КТП на объекты") -> {
                    binding.view1.visibility = View.GONE
                    binding.view2.visibility = View.GONE
                    var path = ""
                    for(item in list) {
                        if(item.name.contains("КТП на объекты, организации")) {
                            path = item.path
                        }
                    }
                    getKTPoFromServer(path)
                }
                name.contains("КТП на сельск") -> {
                    binding.view1.visibility = View.GONE
                    binding.view2.visibility = View.GONE
                    var path = ""
                    for(item in list) {
                        if(item.name.contains("КТП на сельск")) {
                            path = item.path
                        }
                    }
                    getKTPoFromServer(path)
                }
                name.contains("товарищества") -> {
                    binding.view1.visibility = View.GONE
                    binding.view2.visibility = View.GONE
                    var path = ""
                    for(item in list) {
                        if(item.name.contains("товарищества")  && item.name.contains("садов")) {
                            path = item.path
                        }
                    }
                    getKTPoFromServer(path)
                }
                name.contains("ПТП №") -> currentName(list, name)
                name.contains("КТП №") -> currentName(list, name)
                name.contains("Детский сад Колосок") -> currentName(list, name)
                name.contains("МОУ ДОД Дом детского творчества") -> currentName(list, name)
                name.startsWith("с. Ягуново") -> currentName(list, name)
                name.contains("ниссан") -> currentName(list, name)
                name.contains("Субару") -> currentName(list, name)
                name.startsWith("Митсубиши") -> currentName(list, name)
                name.startsWith("Авторынок") -> currentName(list, name)

                name.contains(".docx") -> {
                    val savedList = sharedPrefManager.getListOfIdDocVsd()
                    val url = "https://www.googleapis.com/drive/v3/files/${savedList["docx${name}"]}/?key=AIzaSyA-4vWcdoOK_ZKXeLA2eR0Cls1ELoKaUss&alt=media"
                    Log.e("nngngn", url)
                    Toast.makeText(requireContext(), "Загрузка началась", Toast.LENGTH_SHORT).show()
                    downloadFile(url, name)
                }
                name.contains(".doc") -> {
                    val savedList = sharedPrefManager.getListOfIdDocVsd()
                    val url = "https://www.googleapis.com/drive/v3/files/${savedList["doc${name}"]}/?key=AIzaSyA-4vWcdoOK_ZKXeLA2eR0Cls1ELoKaUss&alt=media"
                    Toast.makeText(requireContext(), "Загрузка началась", Toast.LENGTH_SHORT).show()
                    downloadFile(url, name)
                }
                name.contains(".pdf") -> {
                    val savedList = sharedPrefManager.getListOfIdDocVsd()
                    val url = "https://www.googleapis.com/drive/v3/files/${savedList["pdf${name}"]}/?key=AIzaSyA-4vWcdoOK_ZKXeLA2eR0Cls1ELoKaUss&alt=media"
                    Toast.makeText(requireContext(), "Загрузка началась", Toast.LENGTH_SHORT).show()
                    downloadFile(url, name)
                }
                name.contains(".vsd") -> {
                    val savedList = sharedPrefManager.getListOfIdDocVsd()
                    val url = "https://www.googleapis.com/drive/v3/files/${savedList["vsd${name}"]}/?key=AIzaSyA-4vWcdoOK_ZKXeLA2eR0Cls1ELoKaUss&alt=media"
                    Toast.makeText(requireContext(), "Загрузка началась", Toast.LENGTH_SHORT).show()
                    downloadFile(url, name)
                }
            }
        }

    }

    private suspend fun currentName(list: List<Catalog>, name: String) {
        var currentPath = ""
        for(name1 in list) {
            if(name1.name == name) {
                currentPath = name1.path
            }
        }
        val url = currentPath
        downloadHttpRequest(url)
    }

    suspend fun downloadHttpRequest(url: String) {
        val listOfCatalogs = mutableListOf<String>()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("failureee", e.printStackTrace().toString())
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    response.body?.string()?.let { jsonResponse ->
                        val jsonObject = JSONObject(jsonResponse)
                        val files = jsonObject.getJSONArray("files")
                        val listId = mutableMapOf<String, String>()
                        for (i in 0 until files.length()) {
                            val file = files.getJSONObject(i)
                            val fileId = file.getString("id")
                            val fileName = file.getString("name")
                            val mimeType = file.getString("mimeType")
                            listOfCatalogs.add(fileName)
                            when(mimeType) {
                                "application/msword" -> listId["doc${fileName}"] = fileId
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> listId["docx${fileName}"] = fileId
                                "application/vnd.visio" -> listId["vsd${fileName}"] = fileId
                                "application/pdf" -> listId["pdf${fileName}"] = fileId
                            }
                        }
                        activity?.runOnUiThread {
                            val sharedPrefManager = SharedPrefManager(requireContext())
                            listOfIdDocVsd = listId
                            sharedPrefManager.saveListOfIdDocVsd(listId)
                            adapter = FolderAdapter(listOfCatalogs, this@CatalogFragment)
                            binding.rvFolders.adapter = adapter
                        }
                    }
                }else {
                    Log.e("fgdsd", "Response not successful: ${response.code} ${response.message}")
                }
            }
        })
    }

    suspend fun downloadFile(url: String, fileNames: String) {
        val client = OkHttpClient.Builder()
            .connectTimeout(1800, TimeUnit.SECONDS)
            .readTimeout(1800, TimeUnit.SECONDS)
            .writeTimeout(1800, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(downloadsDir, "${System.currentTimeMillis()}${fileNames}")

                    FileOutputStream(file).use { output ->
                        response.body?.byteStream()?.use { input ->
                            input.copyTo(output)
                        }
                    }

                    openFile(file)
                }
            } catch (e: IOException) {
                Log.e("DownloadFile", "Ошибка при загрузке файла: ${e.message}")
                Toast.makeText(requireContext(), "Ошибка при загрузке файла: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openFile(file: File) {
        Log.e("qweqwer", file.extension.lowercase())
        val mimeType = when (file.extension.lowercase()) {
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "vsd" -> "application/vnd.visio"
            "pdf" -> "application/pdf"
            else -> "*/*"
        }

        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            startActivity(Intent.createChooser(intent, "Открыть с помощью"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Нет приложения для открытия этого файла", Toast.LENGTH_SHORT).show()
            Log.e("OpenFile", "Не найдено приложение для открытия файла: ${e.message}")
        }
    }

    fun getVideoFromCache(context: Context, fileName: String): File? {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, fileName)
        return if (file.exists()) file else null
    }

    fun copy(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }

    }

    private fun getKTPoFromServer(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("failureee", e.printStackTrace().toString())
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    response.body?.string()?.let { jsonResponse ->
                        val jsonObject = JSONObject(jsonResponse)
                        val files = jsonObject.getJSONArray("files")
                        for (i in 0 until files.length()) {
                            val file = files.getJSONObject(i)
                            listOfKTP.add(file.getString("name"))
                        }
                        activity?.runOnUiThread {
                            adapter = FolderAdapter(listOfKTP, this@CatalogFragment)
                            binding.rvFolders.adapter = adapter
                        }
                    }
                }else {
                    Log.e("fgdsd", "Response not successful: ${response.code} ${response.message}")
                }
            }
        })
    }

    private fun getInfoFromServer(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("failureee", e.printStackTrace().toString())
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    response.body?.string()?.let { jsonResponse ->
                        parseResponse(jsonResponse)
                    }
                }else {
                    Log.e("fgdsd", "Response not successful: ${response.code} ${response.message}")
                }
            }
        })
    }

    private fun parseResponse(jsonResponse: String) {
        val jsonObject = JSONObject(jsonResponse)
        val files = jsonObject.getJSONArray("files")
        for (i in 0 until files.length()) {
            val file = files.getJSONObject(i)
            val fileId = file.getString("id")
            val fileName = file.getString("name")
            val mimeType = file.getString("mimeType")
            when {
                mimeType.startsWith("application/vnd.google-apps.folder") -> {
                    val url = "https://www.googleapis.com/drive/v3/files?q=%27$fileId%27+in+parents&fields=files(id,name,mimeType)&key=AIzaSyA-4vWcdoOK_ZKXeLA2eR0Cls1ELoKaUss"
                    viewModel.putFileInRoom(dao, Catalog(name = fileName, path = url))
                    getInfoFromServer(url)
                }
                mimeType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document") -> {
                    val url = "https://drive.google.com/uc?export=download&id=$fileId"
                    viewModel.listOfFiles.add(url)
                    saveStringToPreferences(requireContext(),fileId, url)
                    if(isCache) saveVideoToCache(requireContext(), url, fileName)
                }
                mimeType.startsWith("application/msword") -> {
                    val url = "https://drive.google.com/uc?export=download&id=$fileId"
                    viewModel.listOfFiles.add(url)
                    saveStringToPreferences(requireContext(), fileId, url)
                    if(isCache) saveVideoToCache(requireContext(), url, fileName)
                }
                mimeType.startsWith("application/vnd.visio") -> {
                    val url = "https://drive.google.com/uc?export=download&id=$fileId"
                    viewModel.listOfFiles.add(url)
                    saveStringToPreferences(requireContext(), fileId, url)
                    if(isCache) saveVideoToCache(requireContext(), url, fileName)
                }
                mimeType.startsWith("application/pdf") -> {
                    val url = "https://drive.google.com/uc?export=download&id=$fileId"
                    viewModel.listOfFiles.add(url)
                    saveStringToPreferences(requireContext(), fileId, url)
                    if(isCache) saveVideoToCache(requireContext(), url, fileName)
                }
            }
        }
    }

    fun saveVideoToCache(context: Context, videoUrl: String, fileName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)

            if (file.exists()) {
                withContext(Dispatchers.Main) {
                    Log.d("Cache", "Файл уже существует: $fileName")
                }
                return@launch
            }

            try {
                val inputStream: InputStream = URL(videoUrl).openStream()
                val outputStream: OutputStream = FileOutputStream(file)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                withContext(Dispatchers.Main) {
                    Log.d("Cache", "Файл успешно сохранен: $fileName")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Cache", "Ошибка при сохранении видео: ${e.message}")
                }
            }
        }
    }

    fun saveStringToPreferences(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
        Log.d("Cache", "Файл успешно сохранен: $key")
    }

    fun saveStringFileToPreferences(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringFromPreferences(context: Context, key: String): String? {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    fun getBooleanFromPreferences(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("answer", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("answer", true)
    }

}
