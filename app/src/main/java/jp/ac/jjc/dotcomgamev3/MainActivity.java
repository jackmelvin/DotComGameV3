package jp.ac.jjc.dotcomgamev3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

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
    enum Player {
        USER,
        PC
    }
    TextView tvStatus;
    final int DOTCOMLENGTH = 3;
    final int GRIDSIZE = 49;
    final int GRIDWIDTH = 7;
    final int NUMOFDOTCOM = 3;
    boolean[] grid;
    ArrayList<DotCom> pcDotComList;
    DotComGame game;
    Button[] cells;
//    int color[] = {Color.BLUE, Color.GREEN, Color.BLUE};
//    int currentColor = 0;

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
        pcDotComList = new ArrayList<DotCom>();
        game = new DotComGame();
        game.setUpGame();
    }

    public void setMessage() {
        TextView tvUserMove = findViewById(R.id.tvUserMove);
        TextView tvPcMove = findViewById(R.id.tvPcMove);
        String userMoveMsg = getString(R.string.tv_user_move) + Integer.toString(game.numOfGuesses);
        String pcMoveMsg = getString(R.string.tv_com_move) + Integer.toString(game.pcMoves);
        tvUserMove.setText(userMoveMsg);
        tvPcMove.setText(pcMoveMsg);
    }

    private class Listener implements View.OnClickListener {
        String location;
        public Listener(String loc) {
            location = loc;
        }
        Result result;// = Result.MISS;
        public void onClick(View view) {
            game.numOfGuesses++;
            grid[Integer.parseInt(location)] = true;
            for(DotCom dotCom: pcDotComList) {
                result = dotCom.checkYourself(location, Player.USER);
                if(result == Result.KILL) {
                    pcDotComList.remove(dotCom);
                    break;
                }
                if(result == Result.HIT) {
                    break;
                }
            }
            Button btn = (Button) view;
            if(result == Result.MISS) {
                btn.setTextColor(Color.BLUE);
                btn.setText(getString(R.string.miss));
                game.pcAttack();
            } else {
                btn.setBackgroundColor(Color.RED);
                btn.setTextColor(Color.BLUE);
                btn.setText(getString(R.string.hit));
            }
            setMessage();
            if(pcDotComList.isEmpty()) {
                game.endGame(Player.USER);
            }
            view.setOnClickListener(null);
        }
    }

    private class DotComGame {
        ArrayList<DotCom> userDotComList = new ArrayList<DotCom>();
        private int numOfGuesses = 0;
        private int pcMoves = 0;
        private void setUpGame() {
            setUserDotComs();
            setPcDotComs();
            setMessage();
        }
        private void setPcDotComs() {
            GameHelper helper = new GameHelper();
            DotCom one = new DotCom();
            DotCom two = new DotCom();
            DotCom three = new DotCom();
            pcDotComList.add(one);
            pcDotComList.add(two);
            pcDotComList.add(three);
            for(DotCom dotComToSet : pcDotComList) {
                dotComToSet.setLocationCells(helper.placeDotCom());
            }
            for(DotCom dot : userDotComList) {
                for(int i = 0; i < DOTCOMLENGTH; i++) {
                    int loc = Integer.parseInt(dot.getLocationCells().get(i));
                    grid[loc] = false;
                }
            }
        }
        private void setUserDotComs() {
            DotCom userDotComOne = new DotCom();
            DotCom userDotComTwo = new DotCom();
            DotCom userDotComThree = new DotCom();
            //Default user location
            ArrayList<String> loc1 = new ArrayList<String>(Arrays.asList("1", "2", "3"));
            ArrayList<String> loc2 = new ArrayList<String>(Arrays.asList("0", "7", "14"));
            ArrayList<String> loc3 = new ArrayList<String>(Arrays.asList("42", "43", "44"));
            userDotComOne.setLocationCells(loc1);
            userDotComTwo.setLocationCells(loc2);
            userDotComThree.setLocationCells(loc3);

            //
            userDotComList.add(userDotComOne);
            userDotComList.add(userDotComTwo);
            userDotComList.add(userDotComThree);

            for(DotCom dc : userDotComList) {
                for(int i = 0; i < DOTCOMLENGTH; i++) {
                    int loc = Integer.parseInt(dc.getLocationCells().get(i));
                    System.out.println("User loc: " + loc);
                    grid[loc] = true;
                    cells[loc].setText(getString(R.string.user_alive));
                    cells[loc].setOnClickListener(null);
                }
            }
        }
        private void pcAttack() {
            pcMoves++;
            String randNum = null;
            boolean notDuplicated = false;
            while(!notDuplicated) {
                randNum = Integer.toString((int) (Math.random() * 49));
                if(!grid[Integer.parseInt(randNum)]) {
                    for (DotCom dot : pcDotComList) {
                        Result result = dot.checkYourself(randNum, null);
                        if (result == Result.HIT || result == Result.KILL) {
                            notDuplicated = false;
                            break;
                        } else {
                            notDuplicated = true;
                        }
                    }
                }
            }
            System.out.println(randNum);
            //Check PC attack
            grid[Integer.parseInt(randNum)] = true;
            cells[Integer.parseInt(randNum)].setOnClickListener(null);
            Result result = null;
            for(DotCom dotComToCheck : userDotComList) {
                result = dotComToCheck.checkYourself(randNum, Player.PC);
                if(result == Result.KILL) {
                    userDotComList.remove(dotComToCheck);
                    break;
                }
                if(result == Result.HIT) {
                    break;
                }
            }
            if(result == Result.HIT || result == Result.KILL) {
                cells[Integer.parseInt(randNum)].setTextColor(Color.RED);
                cells[Integer.parseInt(randNum)].setBackgroundColor(Color.BLUE);
                cells[Integer.parseInt(randNum)].setText(getString(R.string.hit));
                if(userDotComList.isEmpty()) {
                    endGame(Player.PC);
                } else {
                    pcAttack();
                }
            } else {
                cells[Integer.parseInt(randNum)].setTextColor(Color.RED);
                cells[Integer.parseInt(randNum)].setText(getString(R.string.miss));
            }
            //End PC attack
        }
        private void endGame(Player who) {
            String message = null;
            if(who == Player.USER) {
                message = getString(R.string.win);
            } else if(who == Player.PC){
                message = getString(R.string.lose);
            }
            tvStatus.setText(message);
            for(Button cell : cells) {
                cell.setOnClickListener(null);
            }
        }
    }

    private class DotCom {
        private ArrayList<String> locationCells;
        public void setLocationCells(ArrayList<String> loc) {
            this.locationCells = loc;
        }
        public ArrayList<String> getLocationCells() {
            return locationCells;
        }
        public Result checkYourself(String userGuess, Player who) {
            String status;
            Result result;
            int index = locationCells.indexOf(userGuess);
            if(index >= 0) {
                locationCells.remove(index);
                result = Result.HIT;
                status = who + getString(R.string.stt_hit);
            } else {
                result = Result.MISS;
                status = who + getString(R.string.stt_miss);
            }
            if(locationCells.isEmpty()) {
                result = Result.KILL;
                status = who + getString(R.string.stt_kill);
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
                //cells[coords[i]].setTextColor(color[currentColor]);
                //cells[coords[i]].setTextColor(Color.RED);
                grid[coords[i]] = true;
                locationCells.add(Integer.toString(coords[i]));
            }
            //currentColor++;
            return locationCells;
        }
    }
}
