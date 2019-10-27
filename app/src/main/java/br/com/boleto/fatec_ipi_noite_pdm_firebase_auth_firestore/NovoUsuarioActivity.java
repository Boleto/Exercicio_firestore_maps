package br.com.boleto.fatec_ipi_noite_pdm_firebase_auth_firestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class NovoUsuarioActivity extends AppCompatActivity {

    private EditText loginNovoUsuarioEditText;
    private EditText senhaNovoUsuarioEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario);
        loginNovoUsuarioEditText =
                findViewById(R.id.loginNovoUsuarioEditText);
        senhaNovoUsuarioEditText =
                findViewById(R.id.senhaNovoUsuarioEditText);
        mAuth = FirebaseAuth.getInstance();
    }

    public void criarNovoUsuario(View view) {
        String login =
                loginNovoUsuarioEditText.getText().toString();
        String senha =
                senhaNovoUsuarioEditText.getText().toString();
        Task<AuthResult> task =
                    mAuth.createUserWithEmailAndPassword(login,senha);
        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(NovoUsuarioActivity.this,
                        getString(android.R.string.ok),
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}
