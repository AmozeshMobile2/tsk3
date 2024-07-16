package ir.wordpress.ivatask

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ir.wordpress.network.NetworkModule

class MainActivity : AppCompatActivity() {

    private val viewModel: WebPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tenthCount = findViewById<TextView>(R.id.first_word_count)
        val tenthCountSequnces = findViewById<TextView>(R.id.two_word_count)
        val countAllKeywordRep = findViewById<TextView>(R.id.third_word_count)

        viewModel.tenthCharacter.observe(this, Observer { character ->
            tenthCount.text = character.toString()
        })

        viewModel.everyTenthCharacterSequence.observe(this, Observer { characters ->
            tenthCountSequnces.text =
                "Sequence of characters:\n" + characters.joinToString(separator = "\n") { "Position ${it.first}: ${it.second}" }
        })

        viewModel.wordCount.observe(this, Observer { wordCount ->
            val result = StringBuilder()
            wordCount.forEach { (word, count) ->
                result.append("$word: $count\n")
            }
            countAllKeywordRep.text = result.toString()
        })


            viewModel.fetchData()

    }
}