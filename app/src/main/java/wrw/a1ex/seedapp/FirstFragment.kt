package wrw.a1ex.seedapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

private lateinit var generateBttn: Button
private lateinit var copyBttn: Button
private lateinit var inputText: TextView
private lateinit var resultText: TextView

class FirstFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        generateBttn =  view?.findViewById(R.id.generate_bttn) as Button
        copyBttn =  view.findViewById(R.id.copy_bttn) as Button
        inputText =  view.findViewById(R.id.inputText) as EditText
        resultText =  view.findViewById(R.id.exportText) as EditText

        return view
    }

    override fun onStart() {
        super.onStart()

        generateBttn.setOnClickListener(){
            var totext = ""
            if (inputText.length()==0)
                Toast.makeText(activity, R.string.error_message, Toast.LENGTH_SHORT).show()

            var string = inputText.text.toString().lowercase().trim()

            if(!isNumber(string)){
                if (!string.contains(" ")){
                  totext =  numberOfWord(string)
                }
                if(string.contains(" ")){
                    string.replace("  "," ")
                    string.replace("   "," ")
                    val array =string.split(" ")
                    totext = numberOfWord(array)
                }
            }
            if (isNumber(string)){
                totext = nomberToString(string)
            }
            resultText.setText(totext)
        }

        copyBttn.setOnClickListener(){
            if(resultText.length()>0){
                val clipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "Ok",
                    resultText.getText().toString()
                )
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(activity, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(activity, R.string.error_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun nomberToString(string: String) : String{

        if(string.length<4) Toast.makeText(activity, R.string.error_message, Toast.LENGTH_SHORT).show()
        val array = string.chunked(4)
        val result = StringBuilder("")
        for(i in array)result.append(numberToWord(i)+" ")
        return result.toString()
    }

    private fun numberToWord(i: String): String {
        val index = i.toInt()
        if (index in 1..2048)
            return Bip39.array[index-1]
        else {Toast.makeText(activity, R.string.error_message, Toast.LENGTH_SHORT).show()
            return getString(R.string.error_message)}
    }

    private fun numberOfWord(array: List<String>): String {
        val x2String = StringBuilder("")
        for (str in array) {
            x2String.append(Bip39.numberOfWord(str))
        }
        return x2String.toString() + ""
    }

    private fun numberOfWord(string: String): String {
        var x = 0
        var count = 0
        for (str in Bip39.array) {
            count++
            if (str == string) x = count
        }
        var x2String = StringBuilder("")
        if (x < 10) x2String = StringBuilder("000").append(x)
        else if (x > 9 && x < 100) x2String =
            StringBuilder("00").append(x)
        else if (x > 99 && x < 1000) x2String =
            StringBuilder("0").append(x)
        else if (x > 999) x2String.append(x)
        return x2String.toString() + ""

    }

    private fun isNumber(string: String) : Boolean =  string.toBigDecimalOrNull() != null

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment FirstFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            FirstFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}