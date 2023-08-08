package com.rgsannn.dailynotes.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.auth.SignInMethodQueryResult;
import com.rgsannn.dailynotes.R;

/**
 * 10120076
 * Rifqi Galih Nur Ikhsan
 * IF-2
 */

public class SignUp extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private TextInputLayout emailTextField;
    private TextInputLayout passwordTextField;
    private TextInputLayout passwordRetypeTextField;
    private Button signUpButton, signInGoogleButton, signInButton;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();

        emailTextField = findViewById(R.id.emailTextField);
        passwordTextField = findViewById(R.id.passwordTextField);
        passwordRetypeTextField = findViewById(R.id.passwordRetypeTextField);
        signUpButton = findViewById(R.id.signUpButton);
        signInGoogleButton = findViewById(R.id.signInGoogleButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpWithEmail();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signInGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(view -> {
            Intent i = new Intent(SignUp.this, SignIn.class);
            startActivity(i);
            finish();
        });

        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in, redirect to main activity
                    redirectToMain();
                }
            }
        };

        // Register the AuthStateListener
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void signUpWithEmail() {
        String email = emailTextField.getEditText().getText().toString().trim();
        String password = passwordTextField.getEditText().getText().toString().trim();
        String passwordRetype = passwordRetypeTextField.getEditText().getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || passwordRetype.isEmpty()) {
            Toast.makeText(SignUp.this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordRetype)) {
            passwordRetypeTextField.setError("Passwords do not match.");
            return;
        }

        // Check if the email is already registered using Firebase methods
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                                // Email is already registered using a different sign-in method
                                emailTextField.setError("Email already registered with a different sign-in method.");
                            } else {
                                // Email is not registered, proceed with email/password sign-up
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign up success, update UI with the signed-in user's information
                                                    Toast.makeText(SignUp.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // If sign up fails, display a message to the user.
                                                    Toast.makeText(SignUp.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Error occurred while checking sign-in methods for the email
                            Toast.makeText(SignUp.this, "Error checking email availability.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Sign the user out before starting the sign-up with Google flow
        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Start the sign-up with Google flow after signing out
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google Sign in failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // This method is called when sign-up is successful
    private void redirectToMain() {
        Intent intent = new Intent(SignUp.this, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp.this, "Google sign in successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp.this, "Google sign in failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}