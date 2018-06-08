package com.example.bankapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bankapp.MyApp;
import com.example.bankapp.R;
import com.example.bankapp.entityRoom.Card;
import com.example.bankapp.entityRoom.CardDao;
import com.example.bankapp.entityRoom.HistoryDao;
import com.example.bankapp.entityRoom.UserDao;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryActivity extends AppCompatActivity {
    @Inject
    CardDao cardDao;
    @Inject
    HistoryDao historyDao;
    @Inject
    UserDao userDao;
    private Disposable dispos;
    private int idCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        MyApp.app().dataBaseComponent().inject(this);
        idCard = getIntent().getIntExtra("idCard", 0);
        getListHistoryCard(idCard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }//onCreateOptionsMenu


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.send_money://перевести

                break;
            case R.id.withdraw_money://снять

                break;
            case R.id.replenish_account://пополнить

                break;
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected


    public void getListHistoryCard(int idCard) {
        dispos = historyDao.allHistoryCard(idCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listCards -> {
                    dispos.dispose();
                   //отображаем список
                });
    }//getListCardsUser

}
