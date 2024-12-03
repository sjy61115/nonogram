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
import android.widget.Toast;
import java.util.ArrayList;
import android.graphics.Color;
import android.widget.CompoundButton;
import android.content.Context;

public class MainActivity extends AppCompatActivity {
    private ToggleButton blackSquareToggle;
    private TextView lifeTextView;
    private int life = 3;
    private Cell[][] buttons = new Cell[5][5];
    private TextView[][] topHints = new TextView[3][5];  // 위쪽 힌트 [3줄][5열]
    private TextView[][] leftHints = new TextView[5][3];  // 왼쪽 힌트 [5행][3줄]
    private TableRow.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면의 너비와 높이 중 작은 값을 기준으로 셀 크기 계산
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int minDimension = Math.min(screenWidth, screenHeight);
        int cellSize = minDimension / 14;

        layoutParams = new TableRow.LayoutParams(cellSize, cellSize);
        layoutParams.setMargins(0, 0, 0, 0);

        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // TextView 크기 조정을 위한 클래스
        class SquareTextView extends androidx.appcompat.widget.AppCompatTextView {
            public SquareTextView(Context context) {
                super(context);
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int size = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), 
                                  View.MeasureSpec.getSize(heightMeasureSpec));
                int finalMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY);
                super.onMeasure(finalMeasureSpec, finalMeasureSpec);
            }
        }

        // 위쪽 힌트를 위한 3줄
        for(int row = 0; row < 3; row++) {
            TableRow hintRow = new TableRow(this);
            hintRow.setGravity(Gravity.CENTER);

            // 왼쪽 여백을 위한 빈 TextView 추가
            for(int i = 0; i < 3; i++) {
                TextView spacer = new SquareTextView(this);
                if(row == 0 && i == 0) {  // 첫 번째 TextView에 Life 표시
                    lifeTextView = spacer;
                    lifeTextView.setText("Life: " + life);
                    lifeTextView.setTextSize(10);
                }
                spacer.setLayoutParams(layoutParams);
                spacer.setGravity(Gravity.CENTER);
                hintRow.addView(spacer);
            }

            // 위쪽 힌트 TextView 생성
            for(int col = 0; col < 5; col++) {
                topHints[row][col] = new SquareTextView(this);
                topHints[row][col].setLayoutParams(layoutParams);
                topHints[row][col].setGravity(Gravity.CENTER);
                hintRow.addView(topHints[row][col]);
            }

            tableLayout.addView(hintRow);
        }

        // 게임 보드 생성
        for(int i = 0; i < 5; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setGravity(Gravity.CENTER);

            // 왼쪽 힌트 TextView 생성
            for(int hint = 0; hint < 3; hint++) {
                leftHints[i][hint] = new SquareTextView(this);
                leftHints[i][hint].setLayoutParams(layoutParams);
                leftHints[i][hint].setGravity(Gravity.CENTER);
                tableRow.addView(leftHints[i][hint]);
            }

            // 버튼 생성
            for(int j = 0; j < 5; j++) {
                buttons[i][j] = new Cell(this);
                buttons[i][j].setLayoutParams(layoutParams);
                
                final int row = i;
                final int col = j;
                
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(blackSquareToggle.isChecked()) {
                            // BLACK SQUARE 모드
                            if(!buttons[row][col].markBlackSquare()) {
                                life--;
                                lifeTextView.setText("Life: " + life);
                                if(life <= 0) {
                                    gameOver();
                                }
                            } else if(Cell.getNumBlackSquares() == 0) {
                                gameWin();
                            }
                        } else {
                            // X 표시 모드
                            buttons[row][col].toggleX();
                        }
                    }
                });
                
                tableRow.addView(buttons[i][j]);
            }
            
            tableLayout.addView(tableRow);
        }

        // BLACK SQUARE 토글 버튼 생성
        blackSquareToggle = new ToggleButton(this);
        blackSquareToggle.setTextOn("BLACK SQUARE");
        blackSquareToggle.setTextOff("BLACK SQUARE");
        blackSquareToggle.setText("BLACK SQUARE");
        TableLayout.LayoutParams toggleParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        );
        toggleParams.topMargin = cellSize / 2;
        toggleParams.gravity = Gravity.CENTER;
        blackSquareToggle.setLayoutParams(toggleParams);
        blackSquareToggle.setBackgroundColor(Color.WHITE);  // 초기 배경색 설정

        // 토글 버튼 상태에 따라 배경색 변경
        blackSquareToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    blackSquareToggle.setBackgroundColor(Color.LTGRAY);  // 눌렸을 때 색상
                } else {
                    blackSquareToggle.setBackgroundColor(Color.WHITE);  // 기본 색상
                }
            }
        });

        tableLayout.addView(blackSquareToggle);

        // 모든 셀이 생성된 후 힌트 계산 및 표시
        updateHints();
    }

    private void updateHints() {
        // 위쪽 힌트 업데이트
        for(int col = 0; col < 5; col++) {
            ArrayList<Integer> hints = new ArrayList<>();
            int count = 0;

            // 연속된 검은색 개수 계산
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

            // 힌트가 없으면 "0" 추가
            if(hints.isEmpty()) {
                topHints[2][col].setText("0");
            } else {
                // 기존 힌트 표시 로직
                for(int i = 0; i < Math.min(hints.size(), 3); i++) {
                    topHints[2-i][col].setText(String.valueOf(hints.get(hints.size()-1-i)));
                }
            }
        }

        // 왼쪽 힌트 업데이트
        for(int row = 0; row < 5; row++) {
            ArrayList<Integer> hints = new ArrayList<>();
            int count = 0;

            // 연속된 검은색 개수 계산
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

            // 힌트가 없으면 "0" 추가
            if(hints.isEmpty()) {
                leftHints[row][2].setText("0");
            } else {
                // 기존 힌트 표시 로직
                for(int i = 0; i < Math.min(hints.size(), 3); i++) {
                    leftHints[row][2-i].setText(String.valueOf(hints.get(hints.size()-1-i)));
                }
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