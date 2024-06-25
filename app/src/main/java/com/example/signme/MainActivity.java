package com.example.signme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ConnectionClass connectionClass;
    Connection con;
    String str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionClass = new ConnectionClass();
        connect();

        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });


        TextView logoText = findViewById(R.id.logoText);
        SpannableString spannableString = new SpannableString("SignMe");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ADD8E6")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Light blue for "Sign"
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 4, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // White for "Me"
        logoText.setText(spannableString);

        TextView registerTextView = findViewById(R.id.registerTextView);
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            // Capture user input
            EditText emailEditText = findViewById(R.id.emailEditText);
            EditText passwordEditText = findViewById(R.id.passwordEditText);

            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check user credentials
            checkUserCredentials(email, password);
        });
    }

    public void connect() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = connectionClass.CONN(); // Establishing database connection
                if (con == null) {
                    str = "Error in connection with MySQL server";
                } else {
                    str = "Connected with MySQL server";
                }
            } catch (Exception e) {
                str = "Error in connection with MySQL server";
                e.printStackTrace(); // Log the exception for debugging
            } finally {
                if (con != null) {
                    try {
                        con.close(); // Close the connection when done
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show(); // Show connection status
            });
        });
    }

    private void checkUserCredentials(String email, String password) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = connectionClass.CONN(); // Establishing database connection
                if (con == null) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error in connection with MySQL server", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Retrieve hashed password from database for the given email
                String query = "SELECT PASSWORD FROM driver WHERE EMAIL = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, email);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String hashedPasswordFromDB = rs.getString("PASSWORD");

                    // Verify the entered password against the hashed password from the database
                    if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Navigate to HomeActivity
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("email", email); // Pass the email to HomeActivity
                            startActivity(intent);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show());
                }

                rs.close();
                stmt.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "SQL Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }


}
