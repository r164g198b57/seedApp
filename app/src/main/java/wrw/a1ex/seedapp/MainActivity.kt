package wrw.a1ex.seedapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

//import kotlinx.android.synthetic.main.activity_main.*     УВАГА!

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = FirstFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container,
                    fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}