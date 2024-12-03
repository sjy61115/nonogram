package com.example.nonograms;

import androidx.appcompat.widget.AppCompatButton;
import android.content.Context;
import androidx.annotation.NonNull;
import android.graphics.Color;
import android.widget.TableRow;


public class Cell extends AppCompatButton{

    boolean blackSquare;
    boolean checked;
    static int numBlackSquares;

    public Cell(@NonNull Context context) {
        super(context);

        // 정사각형 유지를 위한 설정 추가
        setMinimumWidth(0);
        setMinimumHeight(0);
        setPadding(0, 0, 0, 0);

        // selector 배경 설정
        setBackgroundResource(R.drawable.cell_selector);

        // 50% 확률로 검정 사각형으로 설정
        blackSquare = Math.random() < 0.5;
        if (blackSquare) {
            numBlackSquares++;
        }
        checked = false;
    }

    // 검정 사각형 여부 반환
    public boolean isBlackSquare() {
        return blackSquare;
    }

    // 남은 검정 사각형 수 반환
    public static int getNumBlackSquares() {
        return numBlackSquares;
    }

    // 이 사각형을 검정 사각형으 표시
    public boolean markBlackSquare() {
        if (checked) {
            return true; // 이미 체크되어 있으면 실행하지 않음
        }
        if (blackSquare) {
            setBackgroundColor(0xFF000000); // 검정색으로 표시
            setEnabled(false); // 클릭 불가로 설정
            numBlackSquares--;
            return true;
        } else {
            toggleX(); // 검정 사각형이 아니면 X로 표시
            return false;
        }
    }

    // X 표시를 토글
    public boolean toggleX() {
        checked = !checked;
        setText(checked ? "X" : "");
        return checked;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), 
                          MeasureSpec.getSize(heightMeasureSpec));
        int finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(finalMeasureSpec, finalMeasureSpec);
    }
}
