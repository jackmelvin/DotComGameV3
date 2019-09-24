package jp.ac.jjc.dotcomgamev3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        Button btStart = findViewById(R.id.bt_start);
        Button btExit = findViewById(R.id.bt_exit);
        Button btHowToPlay = findViewById(R.id.bt_howToPlay);
        SwitchActivityListener listener = new SwitchActivityListener();
        btStart.setOnClickListener(listener);
        btHowToPlay.setOnClickListener(listener);
        btExit.setOnClickListener(listener);
    }

    public class SwitchActivityListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            Intent intent = null;
            switch(id) {
                case R.id.bt_start:
                    intent = new Intent(StartGameActivity.this, MainActivity.class);
                    break;
                case R.id.bt_howToPlay:
                    intent = new Intent(StartGameActivity.this, HowToPlayActivity.class);
                    break;
                case R.id.bt_exit:
                    System.exit(-1);
            }
            startActivity(intent);
        }
    }
}
