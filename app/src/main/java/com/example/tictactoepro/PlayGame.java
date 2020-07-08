package com.example.tictactoepro;

import android.annotation.SuppressLint;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class PlayGame extends AppCompatActivity {

    FirebaseDatabase Database;
    Dialog dialog1, dialog2;
    int fieldRow = 9;
    private Button [][] buttons = new Button[fieldRow][fieldRow];
    private String [][] bigField = new String[3][3];
    private int next_field_i, next_field_j;
    String You;
    int RoomGame = -1;
    String playerMove = "X";
    String PlayerX = "";
    String PlayerO = "";
    String currentPlayer = "";
    String moveOne = "Y";
    String btn_click = "Y";
    String Win = "";
    boolean exit = false;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playgame);

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                bigField[i][j] = "";

        rules();
        play_game();

        //Кнопка выйти - начало
        Button btn_exit = findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exit();
                    delete_info();
                }
                catch (Exception e)
                {
                    //
                }
            }
        });
        //Кнопка выйти - конец
    }

    //Вывод диалоговых окон с правилами - начало
    private void rules() {
        //Вывод диалогового окна с правилами - начало
        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.previewdialog);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog1.setCancelable(false);

        //кнопка для закрытия диалогового окна - начало
        TextView btn_close = dialog1.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Обрабатываем нажатие кнопки - начало
                try {
                    //Вернуться назад к стартовому экрану - начало
                    Intent intent = new Intent(PlayGame.this, MainActivity.class);
                    delete_info();
                    startActivity(intent);
                    finish();
                    //Вернуться назад к стартовому экрану - конец
                }
                catch (Exception e)
                {
                    //
                }
                dialog1.dismiss();
                dialog2.dismiss();
                //Обрабатываем нажатие кнопки - конец
            }
        });
        //кнопка для закрытия диалогового окна - конец\
        dialog1.show();
        //Кнопка "продолжить" - начало
        Button btn_continue = dialog1.findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.show();
            }
        });
        //Кнопка "продолжить" - конец

        //Вывод диалогового окна с правилами - конец*/



        //Вывод диалогового окна с правилами - начало
        dialog2 = new Dialog(this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.previewdialog2);
        Objects.requireNonNull(dialog2.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setCancelable(false);
        //кнопка для закрытия диалогового окна - начало
        TextView btn_close2 = dialog2.findViewById(R.id.btn_close2);
        btn_close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Обрабатываем нажатие кнопки - начало
                try {
                    //Вернуться назад к стартовому экрану - начало
                    Intent intent = new Intent(PlayGame.this, MainActivity.class);
                    startActivity(intent);
                    delete_info();
                    finish();
                    //Вернуться назад к стартовому экрану - конец
                }
                catch (Exception e)
                {
                    //
                }
                dialog1.dismiss();
                dialog2.dismiss();
                //Обрабатываем нажатие кнопки - конец
            }
        });
        //кнопка для закрытия диалогового окна - конец
        //Кнопка "продолжить" - начало
        Button btn_continue2 = dialog2.findViewById(R.id.btn_continue2);
        btn_continue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                dialog2.dismiss();
            }
        });
        //Кнопка "продолжить" - конец
        //Вывод диалогового окна с правилами - конец*/
    }
    //Вывод диалоговых окон с правилами - конец

    private void play_game() {
        Database = FirebaseDatabase.getInstance();
        final DatabaseReference Ref = Database.getReference("Games");
        final String[][] moves = new String[fieldRow+1][fieldRow+1];

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists())
                    communication_with_the_base(snapshot, Ref, moves);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists())
                    communication_with_the_base(snapshot, Ref, moves);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        Ref.addChildEventListener(childEventListener);
        button();
    }

    private void communication_with_the_base(DataSnapshot snapshot, DatabaseReference Ref, String[][] moves) {
        if (!exit) {
            HashMap<String, String> field = (HashMap<String, String>) snapshot.getValue();
            String key = snapshot.getKey();


            if (RoomGame < 0) {
                assert field != null;
                String value = field.get("PlayerX");
                assert value != null;
                if (value.equals(""))
                    addPlayer(Ref, key, "PlayerX", "X");
                if (RoomGame < 0) {
                    value = field.get("PlayerO");
                    assert value != null;
                    if (value.equals(""))
                        addPlayer(Ref, key, "PlayerO", "O");
                }
            }

            assert key != null;
            if (key.equals(String.valueOf(RoomGame))) {
                assert field != null;
                PlayerX = field.get("PlayerX");
                PlayerO = field.get("PlayerO");
                currentPlayer = field.get("currentPlayer");
                moveOne = field.get("moveOne");
                btn_click = field.get("btn_click");
                Win = field.get("Win");

                String id;
                Set<String> keys = field.keySet();
                String[] arKeys = keys.toArray(new String[field.size()]);
                Collection<String> values = field.values();
                String[] arValues = values.toArray(new String[values.size()]);
                for (int i = 0; i < arKeys.length; i++) {
                    assert arValues[i] != null;
                    if ((!arKeys[i].equals("currentPlayer")) && (!arKeys[i].equals("PlayerX")) && (!arKeys[i].equals("PlayerO")) && (!arKeys[i].equals("moveOne")) && (!arKeys[i].equals("btn_click")) && (!arKeys[i].equals("Win"))) {
                        String[] key_values = arKeys[i].split("_");
                        moves[Integer.parseInt(key_values[0])][Integer.parseInt(key_values[1])] = arValues[i];
                        if (Integer.parseInt(key_values[0]) == 0)
                            id = key_values[1];
                        else
                            id = key_values[0] + key_values[1];
                        Button button = findViewById(Integer.parseInt(id));
                        if (!arValues[i].equals(""))
                            if (button.getText().toString().equals("") && (btn_click.equals("Y"))) {
                                int d = Integer.parseInt(id) / 10;
                                int n = Integer.parseInt(id) % 10;
                                next_field_i = d % 3;
                                next_field_j = n % 3;
                            }
                        button.setText(arValues[i]);
                    }
                    if (arKeys[i].equals("currentPlayer")) {
                        playerMove = arValues[i];
                    }
                }
                initBigField();
                if (!Win.equals("")) {
                    delete_info();
                    Win();
                    exit = true;
                }
            }
        }
    }

    private void button() {
        String id;

        for (int i = 0; i < fieldRow; i++){
            for (int j = 0; j < fieldRow; j++)
            {
                String button_id = "button" + i + "_" + j;
                int resId = getResources().getIdentifier(button_id, "id", getPackageName());
                Button button = findViewById(resId);
                buttons[i][j] = button;
                if (i == 0)
                    id = String.valueOf(j);
                else
                    id = String.valueOf(i) + String.valueOf(j);

                button.setId(Integer.parseInt(id));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!exit){
                        if (exit_of_player())
                            return;
                        Button button = (Button) v;
                        if (!((Button) v).getText().toString().equals(""))
                            return;
                        int VId = v.getId();
                        String ViewIdToString = String.valueOf(VId);

                        if (ViewIdToString.length() == 1)
                            ViewIdToString = "0_" + ViewIdToString;
                        else
                            ViewIdToString = ViewIdToString.charAt(0) + "_" + ViewIdToString.charAt(1);
                        if (check_next_move_and_presence_two_players()) {
                            if (check_right_field(VId)) {
                                addToBase("btn_click", "Y");
                                addToBase(ViewIdToString, playerMove);
                                if (playerMove.equals("X"))
                                    changeUser("O");
                                else
                                    changeUser("X");
                                button.setText(playerMove);
                                button.setClickable(false);
                                if (checkForWinBig(VId)) {
                                    addToBase("Win", You);
                                }
                            } else
                                Toast.makeText(getApplicationContext(), "Вы пошли не втом поле!", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getApplicationContext(), "Противник ще не сделал ход или он не подключен.", Toast.LENGTH_SHORT).show();
                    }
                }

                    private boolean checkForWinBig(int id) {
                        if(checkForWinLittle(id))
                            return check(0, 0, bigField);
                        return false;
                    }

                    private boolean check(int str, int stl, String[][] field) {
                        for (int k = str; k < str + 3; k++)
                            if((field[k][stl].equals(field[k][stl + 1])) && (field[k][stl].equals(field[k][stl + 2])) && (!field[k][stl].equals("")))
                                return true;
                        for (int l = stl; l < stl + 3; l++)
                            if((field[str][l].equals(field[str+1][l])) && (field[str][l].equals(field[str + 2][l])) && (!field[str][l].equals("")))
                                return true;
                        if((field[str][stl].equals(field[str+1][stl + 1])) && (field[str][stl].equals(field[str+2][stl + 2])) && (!field[str][stl].equals("")))
                            return true;
                        return (field[str][stl + 2].equals(field[str+1][stl + 1])) && (field[str][stl + 2].equals(field[str+2][stl])) && (!field[str][stl + 2].equals(""));
                    }

                    private boolean checkForWinLittle(int id) {
                        String[][] field = new String[fieldRow][fieldRow];
                        for (int i = 0; i < fieldRow; i++)
                            for (int j = 0; j < fieldRow; j++)
                                field[i][j] = buttons[i][j].getText().toString();

                        int i = id/10;
                        int j = id%10;

                        if ((i >= 0) && (i <= 2))
                        {
                            if ((j >= 0) && (j <= 2))
                                if (check(0, 0, field)){
                                    button_block(0, 0);
                                    bigField[0][0] = playerMove;
                                    return true;
                                }
                            if ((j >= 3) && (j <= 5))
                                if (check(0, 3, field)) {
                                    button_block(0, 3);
                                    bigField[0][1] = playerMove;
                                    return true;
                                }

                            if ((j >= 6) && (j <= 8))
                                if (check(0, 6,  field))
                                {
                                    button_block(0, 6);
                                    bigField[0][2] = playerMove;
                                    return true;
                                }

                        }

                        if ((i >= 3) && (i <= 5))
                        {
                            if ((j >= 0) && (j <= 2))
                                if (check(3, 0, field)){
                                    button_block(3, 0);
                                    bigField[1][0] = playerMove;
                                    return true;
                                }
                            if ((j >= 3) && (j <= 5))
                                if (check(3, 3, field)) {
                                    button_block(3, 3);
                                    bigField[1][1] = playerMove;
                                    return true;
                                }

                            if ((j >= 6) && (j <= 8))
                                if (check(3, 6,  field)){
                                    button_block(3, 6);
                                    bigField[1][2] = playerMove;
                                    return true;
                                }
                        }

                        if ((i >= 6) && (i <= 8))
                        {
                            if ((j >= 0) && (j <= 2))
                                if (check(6, 0, field)){
                                    button_block(6, 0);
                                    bigField[2][0] = playerMove;
                                    return true;
                                }
                            if ((j >= 3) && (j <= 5))
                                if (check(6, 3, field)){
                                    button_block(6, 3);
                                    bigField[2][1] = playerMove;
                                    return true;
                                }

                            if ((j >= 6) && (j <= 8))
                                if (check(6, 6,  field)){
                                    button_block(6, 6);
                                    bigField[2][2] = playerMove;
                                    return true;
                                }
                        }
                        return false;
                    }

                    private boolean check_next_move_and_presence_two_players() {
                        return PlayerX.equals("Y") && PlayerO.equals("Y") && currentPlayer.equals(You);
                    }

                    private boolean check_right_field(int id) {
                        if (moveOne.equals("Y")){
                            return true;
                        }
                        int i = id/10;
                        int j = id%10;
                        if (!bigField[next_field_i][next_field_j].equals(""))
                            return true;
                        return (((next_field_i * 3) <= i) && ((next_field_i * 3 + 2) >= i)) && (((next_field_j * 3) <= j) && ((next_field_j * 3 + 2) >= j));
                    }

                    private void changeUser (String user)
                    {
                        addToBase("currentPlayer", user);
                        if (user.equals("O") && moveOne.equals("Y"))
                            addToBase("moveOne", "N");
                    }
                });
            }
        }
    }

    private void addPlayer(DatabaseReference Ref, String key, String player, String you) {
        if (moveOne.equals("Y"))
            Ref.child(key).child("moveOne").setValue("Y");
        Ref.child(key).child(String.valueOf(player)).setValue("Y");
        Ref.child(key).child("btn_click").setValue("Y");
        Ref.child(key).child("Win").setValue("");
        You = you;
        Toast.makeText(getApplicationContext(), "Вы играете " + You, Toast.LENGTH_SHORT).show();
        RoomGame = Integer.parseInt(key);
    }

    private void addToBase(String child, String data) {
        DatabaseReference Ref = Database.getReference();
        Ref.child("Games").child(String.valueOf(RoomGame)).child(child).setValue(data);
    }

    private void initBigField() {
        if(RoomGame>=0) {
            for (int str = 0; str < fieldRow; str+=3){
                for (int stl = 0; stl < fieldRow; stl+=3)
                    if ((buttons[str][stl].getText().toString().equals(buttons[str][stl + 1].getText().toString())) && (buttons[str][stl].getText().toString().equals(buttons[str][stl + 2].getText().toString())) && (!buttons[str][stl].getText().toString().equals("")))
                        bigField[str / 3][stl / 3] = buttons[str][stl].getText().toString();

            }
        }
    }

    private void button_block(int str, int stl) {
        for (int i = str; i < str + 3; i++)
            for (int j = stl; j < stl + 3; j++)
            {
                buttons[i][j].setText("");
                addToBase("btn_click", "N");
                String button_id = i + "_" + j;
                int resId = getResources().getIdentifier(button_id, "id", getPackageName());
                Button button = findViewById(resId);
                button.setText(playerMove);
                addToBase(button_id, playerMove);
                button.setClickable(false);
            }

    }

    private void Win() {
        String msg;
        if (Win.equals(You))
            msg = "Вы победили! Поздравляю)\nВыход на главный экран";
        else
            msg = "К сожалению, Вы проиграли.\nВыход на главный экран";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setNegativeButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(PlayGame.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private boolean exit_of_player() {
        if (PlayerX.equals("") && PlayerO.equals("") && currentPlayer.equals("X")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Противник покинул игру. Выход на главный экран");

            alertDialogBuilder.setNegativeButton("Ок", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(PlayGame.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        return false;
    }

    //Тост для кнопки выйти - начало
    private void exit(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Вы действительно хотите выйти? Прогресс текущей игры удалится.");
        alertDialogBuilder.setPositiveButton("Да",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(PlayGame.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        delete_info();
                    }
                });

        alertDialogBuilder.setNegativeButton("Нет",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    //Тост для кнопки выйти - конец

    private void delete_info() {
        if (RoomGame >= 0){
            addToBase("PlayerX", "");
            addToBase("Win", "");
            addToBase("PlayerO", "");
            addToBase("currentPlayer", "X");
            addToBase("moveOne", "Y");
            for (int i = 0; i < fieldRow; i++)
                for (int j = 0; j < fieldRow; j++)
                {
                    String id = i + "_" + j;
                    addToBase(id, "");
                }
        }
    }

    //Системная кнопка "Назад" - начало
    @Override
    public void onBackPressed() {
        try {
            exit();
            delete_info();
        }
        catch (Exception e)
        {
            //
        }
    }
    //Системная кнопка "Назад" - конец
}