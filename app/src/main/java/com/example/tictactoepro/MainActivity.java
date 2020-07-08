package com.example.tictactoepro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_multi_player = findViewById(R.id.button_multi_player);
        button_multi_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    Intent intent = new Intent(MainActivity.this, PlayGame.class);
                    startActivity(intent);
                    finish();
                }
                catch (Exception e)
                {
                    //
                }
            }
        });
    }
    //Системная кнопка "Назад" - начало
    @Override
    public void onBackPressed()
    {
        if ((backPressedTime + 2000) > (System.currentTimeMillis())) {
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else
        {
            backToast = Toast.makeText(getBaseContext(), "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
    //Системная кнопка "Назад" - конец
}
