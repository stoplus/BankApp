package com.example.bankapp.ui;

import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.bankapp.ForDialog;
import com.example.bankapp.MyApp;
import com.example.bankapp.OnItemListener;
import com.example.bankapp.R;
import com.example.bankapp.adapters.AdapterHistory;
import com.example.bankapp.entityRoom.Card;
import com.example.bankapp.entityRoom.CardDao;
import com.example.bankapp.entityRoom.History;
import com.example.bankapp.entityRoom.HistoryDao;
import com.example.bankapp.entityRoom.UserDao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HistoryActivity extends AppCompatActivity implements ForDialog {
    @Inject
    CardDao cardDao;
    @Inject
    HistoryDao historyDao;
    @Inject
    UserDao userDao;
    private Disposable dispos;
    private Disposable disposGetCardById;
    private int idCard;
    private Card tempCard;
    public final static int REPLENISH_CARD = 2000;//пополнить
    public final static int WITHDRAW_MONEY = 2001;//Снять
    public final static int TRANSFER_MONEY = 2002;//перевод
    private boolean flagUpdateCard;
    private History history;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        MyApp.app().dataBaseComponent().inject(this);
        idCard = getIntent().getIntExtra("idCard", 0);
        updateData();
    }


    private void updateData() {
        getListHistoryCard(idCard);//получаем историю этой карты
        getCard(idCard);//заполняем поля номер кары и сумма на карте
    }


    private void getCard(int idCard) {
        disposGetCardById = cardDao.getCardById(idCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(card -> {
                    tempCard = card;
                    disposGetCardById.dispose();
                    TextView cardNumber = findViewById(R.id.idCardNumber);
                    TextView totalAmount = findViewById(R.id.idTotalAmount);
                    cardNumber.setText(card.getCardNumber());
                    totalAmount.setText(String.valueOf(card.getTotalAmount()));

                    if (flagUpdateCard) {
                        tempCard.setTotalAmount(tempCard.getTotalAmount() + history.getAmount());
                        updateCard(tempCard);
                        flagUpdateCard = false;
                    }
                });
    }//getCard


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }//onCreateOptionsMenu


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DialogChangeTotalAmount dialog = new DialogChangeTotalAmount();
        Bundle args = new Bundle();

        int id = item.getItemId();
        switch (id) {
            case R.id.send_money://перевести
                args.putInt("Select_action", TRANSFER_MONEY);
                flagUpdateCard = false;
                break;
            case R.id.withdraw_money://снять
                args.putInt("Select_action", WITHDRAW_MONEY);
                flagUpdateCard = true;
                break;
            case R.id.replenish_account://пополнить
                args.putInt("Select_action", REPLENISH_CARD);
                flagUpdateCard = true;
                break;
        }//switch

        args.putInt("idUser", tempCard.getIdUser());
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "changeTotalAmount");
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected


    public void getListHistoryCard(int idCard) {
        dispos = historyDao.allHistoryCard(idCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listHistory -> {
                    dispos.dispose();
                    //отображаем список
                    AdapterHistory adapter = new AdapterHistory(this, listHistory);
                    RecyclerView recyclerView = findViewById(R.id.idRecyclerHistory);
                    recyclerView.setAdapter(adapter);
                });
    }//getListCardsUser


    @Override
    public void onGetDataFromDialog(String date, int total, int idCardNumberWherefrom, String cardNumberWhere) {
        history = new History(date, total, idCardNumberWherefrom, cardNumberWhere);
        idCard = idCardNumberWherefrom;
        insertHistory(history);
    }

    private void insertHistory(History history) {
        Completable.fromAction(() -> historyDao.insert(history))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        if (flagUpdateCard) {
                            getCard(idCard);
                        } else updateData();
                    }//onComplete

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }


    private void updateCard(Card card) {
        Completable.fromAction(() -> cardDao.update(card))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        updateData();
                    }//onComplete

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }
}
