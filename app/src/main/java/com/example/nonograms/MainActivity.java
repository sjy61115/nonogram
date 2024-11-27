package com.example.nonograms;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.Gravity;
import android.view.View;
import android.widget.ToggleButton;
import com.example.nonograms.Cell;
import android.widget.Toast;
import java.util.ArrayList;
import android.graphics.Color;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {
    private ToggleButton blackSquareToggle;
    private TextView lifeTextView;
    private int life = 3;
    private Cell[][] buttons = new Cell[5][5];
    private TextView[][] topHints = new TextView[3][5];  // 힌트1
    private TextView[][] leftHints = new TextView[5][3];  // 힌트2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // 위 3줄
        for(int row = 0; row < 3; row++) {
            TableRow hintRow = new TableRow(this);
            hintRow.setGravity(Gravity.CENTER);
            
            // 첫줄 Life 표시
            if(row == 0) {
                lifeTextView = new TextView(this);
                lifeTextView.setText("Life: " + life);
                lifeTextView.setTextSize(20);
                TableRow.LayoutParams lifeParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                );
                lifeParams.span = 3;  // 3칸
                lifeTextView.setLayoutParams(lifeParams);
                hintRow.addView(lifeTextView);
            } else {
                // Life에 빈TextView 추가
                for(int i = 0; i < 3; i++) {
                    TextView spacer = new TextView(this);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(150, 150);
                    spacer.setLayoutParams(params);
                    hintRow.addView(spacer);
                }
            }

            // 위쪽 힌트 생성
            for(int col = 0; col < 5; col++) {
                topHints[row][col] = new TextView(this);
                TableRow.LayoutParams params = new TableRow.LayoutParams(150, 150);
                topHints[row][col].setLayoutParams(params);
                topHints[row][col].setGravity(Gravity.CENTER);
                hintRow.addView(topHints[row][col]);
            }
            
            tableLayout.addView(hintRow);
        }

        //보드 생성
        for(int i = 0; i < 5; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setGravity(Gravity.CENTER);

            // 왼쪽 힌트 생성
            for(int hint = 0; hint < 3; hint++) {
                leftHints[i][hint] = new TextView(this);
                TableRow.LayoutParams params = new TableRow.LayoutParams(150, 150);
                leftHints[i][hint].setLayoutParams(params);
                leftHints[i][hint].setGravity(Gravity.CENTER);
                tableRow.addView(leftHints[i][hint]);
            }

            // 버튼 생성
            for(int j = 0; j < 5; j++) {
                buttons[i][j] = new Cell(this);
                TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(150, 150);
                buttonParams.setMargins(0, 0, 0, 0);
                buttons[i][j].setLayoutParams(buttonParams);
                buttons[i][j].setPadding(0, 0, 0, 0);
                
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cell cell = (Cell)v;
                        if (blackSquareToggle.isChecked()) {
                            boolean isCorrect = cell.markBlackSquare();
                            if (!isCorrect) {
                                life--;
                                lifeTextView.setText("Life: " + life);
                                if (life <= 0) {
                                    gameOver();
                                }
                            } else {
                                if (Cell.getNumBlackSquares() == 0) {
                                    gameWin();
                                }
                            }
                        } else {
                            cell.toggleX();
                        }
                    }
                });
                
                tableRow.addView(buttons[i][j]);
            }
            tableLayout.addView(tableRow);
        }

        //토글 버튼
        blackSquareToggle = new ToggleButton(this);
        blackSquareToggle.setTextOn("BLACK SQUARE");
        blackSquareToggle.setTextOff("BLACK SQUARE");
        blackSquareToggle.setText("BLACK SQUARE");
        blackSquareToggle.setBackgroundColor(Color.LTGRAY);
        
        // 토글상태변경 리스너
        blackSquareToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    blackSquareToggle.setBackgroundColor(Color.DKGRAY);  //켜짐
                    blackSquareToggle.setTextColor(Color.WHITE);
                } else {
                    blackSquareToggle.setBackgroundColor(Color.LTGRAY);  //꺼짐
                    blackSquareToggle.setTextColor(Color.BLACK);
                }
            }
        });

        TableLayout.LayoutParams toggleParams = new TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        );
        toggleParams.topMargin = 50;
        blackSquareToggle.setLayoutParams(toggleParams);
        tableLayout.addView(blackSquareToggle);

        //셀 생성 후 힌트표시
        updateHints();
    }

    private void updateHints() {
        //위 힌트 업뎃
        for(int col = 0; col < 5; col++) {
            ArrayList<Integer> hints = new ArrayList<>();
            int count = 0;
            
            //연속 박스계산
            for(int row = 0; row < 5; row++) {
                if(buttons[row][col].isBlackSquare()) {
                    count++;
                } else if(count > 0) {
                    hints.add(count);
                    count = 0;
                }
            }
            if(count > 0) {
                hints.add(count);
            }
            
            //힌트
            for(int i = 0; i < Math.min(hints.size(), 3); i++) {
                topHints[2-i][col].setText(String.valueOf(hints.get(hints.size()-1-i)));
            }
        }

        //왼쪽힌트 업뎃
        for(int row = 0; row < 5; row++) {
            ArrayList<Integer> hints = new ArrayList<>();
            int count = 0;
            
            //연속박스 계산
            for(int col = 0; col < 5; col++) {
                if(buttons[row][col].isBlackSquare()) {
                    count++;
                } else if(count > 0) {
                    hints.add(count);
                    count = 0;
                }
            }
            if(count > 0) {
                hints.add(count);
            }
            
            //힌트 
            for(int i = 0; i < Math.min(hints.size(), 3); i++) {
                leftHints[row][2-i].setText(String.valueOf(hints.get(hints.size()-1-i)));
            }
        }
    }

    private void gameOver() {
        Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show();
        for (Cell[] row : buttons) {
            for (Cell cell : row) {
                cell.setEnabled(false);
            }
        }
        blackSquareToggle.setEnabled(false);
    }

    private void gameWin() {
        Toast.makeText(this, "WIN!", Toast.LENGTH_LONG).show();
        for (Cell[] row : buttons) {
            for (Cell cell : row) {
                cell.setEnabled(false);
            }
        }
        blackSquareToggle.setEnabled(false);
    }
}