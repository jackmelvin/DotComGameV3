package jp.ac.jjc.dotcomgamev3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HowToPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        Button btToMainMenu = findViewById(R.id.btToMainMenu);
        Listener listener = new Listener();
        btToMainMenu.setOnClickListener(listener);
    }

    public class Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HowToPlayActivity.this, StartGameActivity.class);
            startActivity(intent);
        }
    }
}
