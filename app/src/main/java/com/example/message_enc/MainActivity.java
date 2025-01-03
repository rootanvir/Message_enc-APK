package com.example.message_enc;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.ClipData;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText txtMessage, txtKey;
    private TextView txtOutput;
    private Button btnEnc, btnDec, btnClear, btnCopy, btnPaste;
    private LinearLayout Top, bottom;
    private Switch hideNseek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        txtMessage = findViewById(R.id.txtMessage);
        txtKey = findViewById(R.id.txtKey);
        txtOutput = findViewById(R.id.txtOutput);
        btnEnc = findViewById(R.id.btnEnc);
        btnDec = findViewById(R.id.btnDec);
        btnClear = findViewById(R.id.btnClear);
        btnCopy = findViewById(R.id.btnCopy);
        btnPaste = findViewById(R.id.btnPaste);
        hideNseek = findViewById(R.id.hideNseek);
        Top = findViewById(R.id.Top);
        bottom = findViewById(R.id.bottom);

        // Set button click listeners
        btnEnc.setOnClickListener(v -> handleEncrypt());
        btnDec.setOnClickListener(v -> handleDecrypt());
        btnClear.setOnClickListener(v -> handleClear());
        btnCopy.setOnClickListener(v -> handleCopy());
        btnPaste.setOnClickListener(v -> handlePaste());

        // Set switch listener
        hideNseek.setOnCheckedChangeListener((buttonView, isChecked) -> toggleLayouts(isChecked));
    }

    private void toggleLayouts(boolean hide) {
        if (hide) {
            Top.setVisibility(View.GONE);
            bottom.setVisibility(View.GONE);
            Toast.makeText(this, "You can't see me now !", Toast.LENGTH_SHORT).show();
        } else {
            Top.setVisibility(View.VISIBLE);
            bottom.setVisibility(View.VISIBLE);
            Toast.makeText(this, "You can see me now !", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleEncrypt() {
        String message = txtMessage.getText().toString();
        String key = txtKey.getText().toString();

        if (validateInput(message, key)) {
            String encryptedMessage = encrypt(message, key);
            txtOutput.setText(encryptedMessage);
        }
    }

    private void handleDecrypt() {
        String message = txtMessage.getText().toString();
        String key = txtKey.getText().toString();

        if (validateInput(message, key)) {
            String decryptedMessage = decrypt(message, key);
            txtOutput.setText(decryptedMessage);
        }
    }

    private void handleClear() {
        txtMessage.setText("");
        txtKey.setText("");
        txtOutput.setText("");
        Toast.makeText(this, "Fields cleared", Toast.LENGTH_SHORT).show();
    }

    private void handleCopy() {
        String outputText = txtOutput.getText().toString();
        if (!TextUtils.isEmpty(outputText)) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Encrypted/Decrypted Message", outputText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Output copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Output is empty, nothing to copy", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePaste() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            ClipData clip = clipboard.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                String pasteData = clip.getItemAt(0).getText().toString();
                txtMessage.setText(pasteData);
                Toast.makeText(this, "Pasted to message box", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Clipboard is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Clipboard is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String message, String key) {
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(key)) {
            Toast.makeText(this, "Key cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String encrypt(String str, String kk) {
        int[] key = new int[kk.length()];
        for (int i = 0; i < kk.length(); i++) {
            key[i] = kk.charAt(i) % 48;
        }

        StringBuilder msg = new StringBuilder();
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (j == kk.length()) {
                j = 0;
            }
            // Safe zone modulo: ASCII range 32–126
            char ch = (char) ((str.charAt(i) + key[j] - 32) % 95 + 32);
            msg.append(ch);
            j++;
        }
        return msg.toString();
    }

    private String decrypt(String str, String kk) {
        int[] key = new int[kk.length()];
        for (int i = 0; i < kk.length(); i++) {
            key[i] = kk.charAt(i) % 48;
        }

        StringBuilder msg = new StringBuilder();
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (j == kk.length()) {
                j = 0;
            }
            // Safe zone modulo: ASCII range 32–126
            char ch = (char) ((str.charAt(i) - key[j] - 32 + 95) % 95 + 32);
            msg.append(ch);
            j++;
        }
        return msg.toString();
    }
}
