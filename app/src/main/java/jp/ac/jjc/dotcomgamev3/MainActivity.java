package jp.ac.jjc.dotcomgamev3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    enum Result {
        HIT,
        MISS,
        KILL
    }
    enum Orientation {
        HORIZONTAL,
        VERTICAL
    }
    TextView tvStatus;
    final int DOTCOMLENGTH = 3;
    final int GRIDSIZE = 49;
    final int GRIDWIDTH = 7;
    final int NUMOFDOTCOM = 3;
    boolean[] grid;
    ArrayList<DotCom> dotComList;
    DotComGame game;
    Button[] cells;
    int color[] = {Color.RED, Color.GREEN, Color.BLUE};
    int currentColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cells = new Button[GRIDSIZE];
        for(int i = 0; i < GRIDSIZE; i++) {
            cells[i] = findViewById(R.id.cell00 + i);
            cells[i].setOnClickListener(new Listener(Integer.toString(i)));
        }
        tvStatus = findViewById(R.id.tvStatus);
        grid = new boolean[GRIDSIZE];
        dotComList = new ArrayList<DotCom>();
        game = new DotComGame();
        game.setUpGame();

    }

    public void setMessage() {
        TextView tvAttempt = findViewById(R.id.tvAttempt);
        TextView tvKilled = findViewById(R.id.tvKilled);
        TextView tvLeft = findViewById(R.id.tvLeft);
        String attemptMsg = getString(R.string.tv_attempt) + Integer.toString(game.numOfGuesses);
        String leftMsg = getString(R.string.tv_dotComLeft) + Integer.toString(dotComList.size());
        String killedMsg = getString(R.string.tv_dotComKilled) + Integer.toString(NUMOFDOTCOM - dotComList.size());
        tvAttempt.setText(attemptMsg);
        tvKilled.setText(killedMsg);
        tvLeft.setText(leftMsg);
    }
    // ボタンの文字を買えるメソッド
//    public void View_setText(View view, String text) {
//        Button btn = (Button) view;
//        btn.setText(text);
//    }

    private class Listener implements View.OnClickListener {
        String location;
        public Listener(String loc) {
            location = loc;
        }
        Result result;// = Result.MISS;
        public void onClick(View view) {
            game.numOfGuesses++;

            for(DotCom dotCom: dotComList) {
                result = dotCom.checkYourself(location);
                if(result == Result.KILL) {
                    dotComList.remove(dotCom);
                    break;
                }
                if(result == Result.HIT) {
                    break;
                }
            }
            Button btn = (Button) view;
            if(result == Result.MISS) {
                btn.setText(getString(R.string.miss));
            } else {
                btn.setText(getString(R.string.hit));
            }
            setMessage();
            String message;
            if(dotComList.isEmpty()) {
                message = getString(R.string.endGame_Front) + game.numOfGuesses + getString(R.string.endGame_End);
                tvStatus.setText(message);
                for(Button cell : cells) {
                    cell.setOnClickListener(null);
                }
            }
            view.setOnClickListener(null);
        }
    }

    private class DotComGame {
        private int numOfGuesses = 0;
        private void setUpGame() {
            GameHelper helper = new GameHelper();
            DotCom one = new DotCom();
            DotCom two = new DotCom();
            DotCom three = new DotCom();
            dotComList.add(one);
            dotComList.add(two);
            dotComList.add(three);
            for(DotCom dotComToSet : dotComList) {
                dotComToSet.setLocations(helper.placeDotCom());
            }
            setMessage();
        }
    }

    private class DotCom {
        private ArrayList<String> locationCells;
        public void setLocations(ArrayList<String> loc) {
            this.locationCells = loc;
        }

        public Result checkYourself(String userGuess) {
            String status;
            Result result;
            int index = locationCells.indexOf(userGuess);
            if(index >= 0) {
                locationCells.remove(index);
                result = Result.HIT;
                status = getString(R.string.stt_hit);
            } else {
                result = Result.MISS;
                status = getString(R.string.stt_miss);
            }
            if(locationCells.isEmpty()) {
                result = Result.KILL;
                status = getString(R.string.stt_kill);
            }
            tvStatus.setText(status);
            return result;
        }
    }

    private class GameHelper {
        int dotComCount;

        public ArrayList<String> placeDotCom() {

            boolean success = false;
            int location = 0;
            Orientation orientation = (dotComCount % 2 == 0) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            int incr = (orientation == Orientation.HORIZONTAL) ? 1 : GRIDWIDTH;
            int[] coords = new int[DOTCOMLENGTH];
            while(!success) {
                location = (int)(Math.random() * GRIDSIZE);
                //print for debugging
                String rotationStr = (orientation == Orientation.HORIZONTAL) ? " horizontal" : " vertical";
                System.out.println("Try " + location + rotationStr);
                //
                int i = 0;
                success = true;
                while(success && i < DOTCOMLENGTH) {
                    if(!grid[location]) {
                        coords[i++] = location;
                        location += incr;
                        if(i > 0 && i < 3) {
                            if((orientation == Orientation.HORIZONTAL) && (location % GRIDWIDTH == 0)) {
                                success = false;
                            }
                            if(location >= GRIDSIZE) {
                                success = false;
                            }
                        }
                    } else {
                        System.out.println("Used");
                        success = false;
                    }
                }
            }
            dotComCount++;
            ArrayList<String> locationCells = new ArrayList<String>();
            for(int i = 0; i < DOTCOMLENGTH; i++) {
                cells[coords[i]].setTextColor(color[currentColor]);
                grid[coords[i]] = true;
                locationCells.add(Integer.toString(coords[i]));
            }
            currentColor++;
            return locationCells;
        }
    }
}
