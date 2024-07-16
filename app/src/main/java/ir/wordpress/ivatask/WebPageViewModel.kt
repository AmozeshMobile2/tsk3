package ir.wordpress.ivatask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.wordpress.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class WebPageViewModel : ViewModel() {

     val url = "https://blog.mindorks.com/what-are-the-different-protection-levels-in-android-permission/"

    private val _tenthCharacter = MutableLiveData<Char>()
    val tenthCharacter: LiveData<Char> get() = _tenthCharacter

    private val _everyTenthCharacterSequence = MutableLiveData<List<Pair<Int, Char>>>()
    val everyTenthCharacterSequence: LiveData<List<Pair<Int, Char>>> get() = _everyTenthCharacterSequence
    private val _wordCount = MutableLiveData<Map<String, Int>>()
    val wordCount: LiveData<Map<String, Int>> get() = _wordCount

    fun fetchData() {
        viewModelScope.launch {
            val content = NetworkModule.fetchContent(url)
              //  fetchContent(url)

            content?.let {
                val plainText = Jsoup.parse(it).text()
                val contentWithoutWhitespace = plainText.replace("\\s".toRegex(), "")

                if (contentWithoutWhitespace.length >= 10) {
                    val tenthCharacter = contentWithoutWhitespace[9]
                    _tenthCharacter.postValue(tenthCharacter)
                }

                val charactersDeferred = async { processContent(contentWithoutWhitespace) }
                val wordCountDeferred = async { countWords(plainText) }

                val characters = charactersDeferred.await()
                val wordCount = wordCountDeferred.await()

                _everyTenthCharacterSequence.postValue(characters)
                _wordCount.postValue(wordCount)
            }
        }
    }

    private suspend fun processContent(content: String): List<Pair<Int, Char>> {
        return withContext(Dispatchers.Default) {
            val characters = mutableListOf<Pair<Int, Char>>()
            for (i in 9 until content.length step 10) {
                characters.add(Pair(i + 1, content[i]))
            }
            characters
        }
    }

    private suspend fun countWords(content: String): Map<String, Int> {
        return withContext(Dispatchers.Default) {
            val words = content.split("\\W+".toRegex())
            val wordCount = mutableMapOf<String, Int>()
            for (word in words) {
                if (word.isNotBlank()) {
                    wordCount[word] = wordCount.getOrDefault(word, 0) + 1
                }
            }
            wordCount
        }
    }
}