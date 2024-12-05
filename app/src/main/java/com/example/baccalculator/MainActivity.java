package com.example.baccalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baccalculator.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private int bac;
    ActivityMainBinding binding;
    ArrayList<Drink> drinks=new ArrayList<>();
    Profile profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                binding.textseek.setText(""+i+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value=seekBar.getProgress();
            }
        });


        binding.btnsetweight.setOnClickListener(view -> {

            String enteredweight=binding.edtweight.getText().toString();
            try {
                double weight = Double.parseDouble(enteredweight);
                if(weight>0) {
                    String gender = "female";
                    if (binding.radiogrp.getCheckedRadioButtonId() == R.id.rdmale) {
                        gender = "male";
                    }
                    profile = new Profile(gender, weight);

                    binding.txtweight.setText(weight + " " + (gender));
                    binding.edtweight.setText("");
                }else{
                    Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
                }
                drinks.clear();
                calculateBac();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });



        binding.btnadddrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(profile==null){
                    Toast.makeText(MainActivity.this, "Set up your weight and gender", Toast.LENGTH_SHORT).show();
                }else{
                    double size=1;
                    if(binding.radiogrp.getCheckedRadioButtonId()==R.id.btn5oz){
                        size=5;
                    }else if(binding.radiogrp.getCheckedRadioButtonId()==R.id.btn12oz){
                        size=12;

                    }
                    double percentage=binding.seekbar.getProgress();
                    if(percentage>0){
                        Drink drink=new Drink(size,percentage);
                        drinks.add(drink);
                        calculateBac();



                    }else{
                        Toast.makeText(MainActivity.this, "set up the alcohol percentage", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });



        binding.btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.txtweight.setText("");
                binding.edtweight.setText("");
                binding.rdmale.setActivated(false);
                binding.rdfemale.setActivated(false);
                binding.btnadddrink.isEnabled();
            }
        });

    }

    void calculateBac() {
        binding.txtdrink.setText(String.valueOf(drinks.size()));
        if (drinks.size() == 0) {
            binding.txtlvl.setText(String.valueOf(0.00));
            binding.txtstatus.setText("You are safe");
            binding.txtstatus.setBackgroundColor(getColor(R.color.green));

        } else {
            double valueA = 0.0;
            for (Drink drink : drinks) {
                valueA = valueA + (drink.getSize() * drink.getPercentage()) / 100.0;

            }
            double r = 0.73;
            if (Objects.equals(profile.getGender(), "female")) {
                r = 0.66;
            }


            double bac = valueA * 5.14 / (profile.getWeight() * r);
            binding.txtlvl.setText(String.format("%.2f",bac));
            if(bac<=0.08){
                binding.txtstatus.setText("You are safe");
                binding.txtstatus.setBackgroundColor(getColor(R.color.green));
            }else if (0.2 >=bac){
                binding.txtstatus.setText("Be careful");
                binding.txtstatus.setBackgroundColor(getColor(R.color.yellow));
            }else {
                binding.txtstatus.setText("Over the limit");
                binding.txtstatus.setBackgroundColor(getColor(R.color.red));
            }



        }
    }
}