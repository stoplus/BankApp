package com.example.bankapp.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bankapp.MyApp;
import com.example.bankapp.R;
import com.example.bankapp.entityRoom.User;
import com.example.bankapp.entityRoom.UserDao;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    @Inject
    UserDao userDao;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView singInText;
    private TextView registerText;
    private Button mEmailSignInButton;
    private Button mRegisterButton;
    private boolean flagSinIn;
    private View view;
    private Disposable dispos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().inflate(R.layout.activity_login, null);
        setContentView(view);

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mRegisterButton = findViewById(R.id.email_register_button);
        mEmailSignInButton = findViewById(R.id.sign_in_button);
        singInText = findViewById(R.id.singInText);
        registerText = findViewById(R.id.registerText);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        setSingIn();
        MyApp.app().dataBaseComponent().inject(this);
    }//onCreate


    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            if (flagSinIn) {
                getListUsers(email, password);
            } else insertUser(new User(email, password));
        }
    }//attemptLogin


    public void getListUsers(String email, String password) {
        dispos = userDao.allUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listUsers -> {
                    dispos.dispose();
                    getResultSingIn(email, password, listUsers);
                });
    }//getListImageObj


    private void getResultSingIn(String email, String password, List<User> listUsers) {
        boolean flag = true;
        Intent intent = null;
        for (int i = 0; i < listUsers.size(); i++) {
            if (listUsers.get(i).getEmail().equals(email) &&
                    listUsers.get(i).getPassword().equals(password)) {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("idUser", listUsers.get(i).getId());
                flag = false;
                break;
            } else flag = true;
        }//for

        showProgress(false);
        if (flag) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        } else {
            startActivity(intent);
            finish();
        }
    }//getResultSingIn


    private void insertUser(final User user) {
        Completable.fromAction(() -> userDao.insert(user))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {//Вставляем новую
                        showProgress(false);
                        setSingIn();
                        Snackbar.make(view, getResources().getString(R.string.successful_register)
                                , Snackbar.LENGTH_INDEFINITE).show();
                    }//onComplete

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }//addProductForList


    private boolean isEmailValid(String email) {
        // TODO: 08.06.2018 добавить точку
        return email.contains("@");
    }//isEmailValid


    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }//isPasswordValid


    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }//showProgress


    public void register(View view) {
        singInText.setVisibility(View.VISIBLE);
        mEmailSignInButton.setVisibility(View.GONE);
        registerText.setVisibility(View.GONE);
        mRegisterButton.setVisibility(View.VISIBLE);
        flagSinIn = false;
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(getResources().getString(R.string.title_activity_register));
    }//register


    public void singIn(View view) {
        setSingIn();
    }//singIn


    private void setSingIn() {
        singInText.setVisibility(View.GONE);
        mEmailSignInButton.setVisibility(View.VISIBLE);
        registerText.setVisibility(View.VISIBLE);
        mRegisterButton.setVisibility(View.GONE);
        flagSinIn = true;
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(getResources().getString(R.string.title_activity_login));
    }//setSingIn
}//class LoginActivity

