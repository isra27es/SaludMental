import android.content.Context
import android.util.Log
import com.example.psico.R
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject


class DialogflowService(private val context: Context) {

    private suspend fun loadCredentials(): GoogleCredentials {
        return withContext(Dispatchers.IO) {
            val inputStream = context.resources.openRawResource(R.raw.dialogflow_credentials)
            val credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

            // Refresca el token si es necesario
            credentials.refreshIfExpired()
            if (credentials.accessToken == null) {
                credentials.refreshAccessToken()
            }

            credentials
        }
    }

    suspend fun sendMessageToDialogflow(sessionId: String, message: String): String {
        return withContext(Dispatchers.IO) {
            val credentials = loadCredentials() // Carga las credenciales aquí
            val projectId = "oceanic-maker-440919-v0" // Reemplaza con tu ID de proyecto
            val sessionUrl = "https://dialogflow.googleapis.com/v2/projects/oceanic-maker-440919-v0/agent/sessions/$sessionId:detectIntent"

            Log.d("DialogflowService", "URL de solicitud: $sessionUrl")

            // Refresca el token antes de usarlo
            credentials.refreshIfExpired()
            if (credentials.accessToken == null) {
                credentials.refreshAccessToken()
            }

            val url = URL(sessionUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer ${credentials.accessToken.tokenValue}")
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

            val requestBody = """
            {
              "queryInput": {
                "text": {
                  "text": "$message",
                  "languageCode": "es"
                }
              }
            }
        """.trimIndent()

            connection.doOutput = true
            connection.outputStream.write(requestBody.toByteArray(Charsets.UTF_8))

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                // Extrae fulfillmentText del JSON de la respuesta
                val jsonResponse = JSONObject(response)
                val fulfillmentText = jsonResponse
                    .getJSONObject("queryResult")
                    .getString("fulfillmentText")

                fulfillmentText // Devuelve solo fulfillmentText
            } else {
                throw Exception("Error en la solicitud a Dialogflow: Código de respuesta $responseCode")
            }
        }
    }

}
