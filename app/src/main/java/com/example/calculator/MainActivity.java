package com.example.calculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.mariuszgromada.math.mxparser.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText display;
    private EditText oldDisplay;

    //used to count the amount of parenthesis
    int openPar = 0;
    int closedPar = 0;

    //had to use space for phones with edges like samsung
    Boolean space = false;//->for display

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //the two textview on the app one the prev arithmetic->oldDisplay new arithmetic->display
        display = findViewById(R.id.input);
        oldDisplay = findViewById(R.id.oldInput);
        EditText oldDisplayTxt = (EditText) findViewById(R.id.oldInput);
        oldDisplayTxt.setTextDirection(View.TEXT_DIRECTION_LTR);
        display.setShowSoftInputOnFocus(false);


        //the method used to save the preferred mode: night mode, light mode
        SharedPreferences appSettingPrefs = getSharedPreferences(getString(R.string.pref), 0);
        SharedPreferences.Editor editor = appSettingPrefs.edit();
        Boolean isNight = appSettingPrefs.getBoolean("NightMode", false);
        if(isNight){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        ImageButton theme = (ImageButton) findViewById(R.id.theme);
        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNight){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("NightMode", false);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("NightMode", true);
                }
                editor.apply();
            }
        });
    }
    private void updateText(String strToAdd){ //function used to save the input to display textview
        String oldStr = display.getText().toString();
        int cursorPos = display.getSelectionStart();
        String leftStr = oldStr.substring(0, cursorPos);
        String rightStr = oldStr.substring(cursorPos);
        if(space == false){
            display.setText(String.format("%s%s%s%s", leftStr,strToAdd,rightStr, "   "));
            space = true;
        }
        else{
            display.setText(String.format("%s%s%s", leftStr,strToAdd,rightStr));
        }
        display.setSelection(cursorPos+1);
    }
    //basic button methods:
    public void zeroButton(View view){
        updateText("0");
    }
    public void point(View view){
        updateText(".");
    }
    public void plus(View view){
        updateText("+");
    }
    public void one(View view){
        updateText("1");
    }
    public void two(View view){
        updateText("2");
    }
    public void three(View view){
        updateText("3");
    }
    public void minus(View view){
        updateText("-");
    }
    public void six(View view){
        updateText("6");
    }
    public void five(View view){
        updateText("5");
    }
    public void four(View view){
        updateText("4");
    }
    public void multiply(View view){
        updateText("×");
    }
    public void nine(View view){
        updateText("9");
    }
    public void eight(View view){
        updateText("8");
    }
    public void seven(View view){
        updateText("7");
    }
    public void divide(View view){
        updateText("/");
    }
    public void exponent(View view){
        updateText("^");
    }
    //more complex button methods
    public void parentheses(View view) {
        int cursorPos = display.getSelectionStart();
        int textLen = display.getText().length();
        if (closedPar == openPar || display.getText().toString().substring(textLen - 1, textLen).equals("(")){
            updateText("(");
            openPar++;
        }
        else if (closedPar < openPar && !display.getText().toString().substring(textLen - 1, textLen).equals("(")){
            updateText(")");
            closedPar++;
        }
        display.setSelection(cursorPos + 1);
    }
    public void equal(View view){
        String userExp = display.getText().toString();
        String user2 = userExp;
        userExp = userExp.replaceAll("×", "*");
        Expression exp = new Expression(userExp);
        String result = String.valueOf(exp.calculate());
        if(openPar != closedPar){
            oldDisplay.setText("error: parenthesis don't match       ");
        }
        else if(result.equals("NaN")){
            oldDisplay.setText("syntax error       ");
        }
        else{
            oldDisplay.setText(user2.replace("   ", "") + " =       ");
        }
        openPar = 0;
        closedPar = 0;
        display.setText(result + "   ");
        display.setSelection(result.length());
    }
    public void clear(View view){
        int cursorPos = display.getSelectionStart();
        int textLen = display.getText().length();
        int textLen2 = oldDisplay.getText().length();
        if (textLen2 != 0) {
            oldDisplay.setText("");
        }
        if (textLen != 0 && cursorPos != 0) {
            SpannableStringBuilder selection = (SpannableStringBuilder) display.getText();
            selection.replace(0, cursorPos, "");
            display.setText(selection);
            display.setSelection(0);
        }
        openPar = 0;
        closedPar = 0;
    }
    public void backSlash(View view){
        int cursorPos = display.getSelectionStart();
        int textLen = display.getText().length();
        if (textLen != 0 && cursorPos != 0) {
            if(display.getText().toString().substring(cursorPos - 1, cursorPos).equals(")")){
                closedPar--;
            }
            else if(display.getText().toString().substring(cursorPos - 1, cursorPos).equals(")")){
                openPar--;
            }
            SpannableStringBuilder selection = (SpannableStringBuilder) display.getText();
            selection.replace(cursorPos-1, cursorPos, "");
            display.setText(selection);
            display.setSelection(cursorPos-1);
        }
    }
}