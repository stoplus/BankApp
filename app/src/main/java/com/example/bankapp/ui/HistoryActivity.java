package com.example.bankapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.bankapp.ForDialog;
import com.example.bankapp.MyApp;
import com.example.bankapp.R;
import com.example.bankapp.adapters.AdapterHistory;
import com.example.bankapp.entityRoom.Card;
import com.example.bankapp.entityRoom.CardDao;
import com.example.bankapp.entityRoom.History;
import com.example.bankapp.entityRoom.HistoryDao;
import com.example.bankapp.entityRoom.UserDao;

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
    private Disposable disposListHistoryCard;
    private Disposable disposGetCardById;
    private Disposable disposListCardsUser;
    private int idCard;
    private Card tempCard;
    public final static int REPLENISH_CARD = 2000;//пополнить
    public final static int WITHDRAW_MONEY = 2001;//Снять
    public final static int TRANSFER_MONEY = 2002;//перевод
    private boolean flagUpdateCard;
    //    private boolean flagUpdateRecipientCard;
    private History history;
    private int selectedAction;


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
                flagUpdateCard = true;
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
        disposListHistoryCard = historyDao.allHistoryCard(idCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listHistory -> {
                    disposListHistoryCard.dispose();
                    //отображаем список
                    AdapterHistory adapter = new AdapterHistory(this, listHistory);
                    RecyclerView recyclerView = findViewById(R.id.idRecyclerHistory);
                    recyclerView.setAdapter(adapter);
                });
    }//getListCardsUser


    @Override
    public void onGetDataFromDialog(String date, int total, int idCardNumberWherefrom, String cardNumberWhere, int selectedAction, boolean replenishment) {
        history = new History(date, total, idCardNumberWherefrom, cardNumberWhere, replenishment);
        this.selectedAction = selectedAction;
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
                        if (idCard == history.getIdSenderCard()) updateData();
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
                        if (card == tempCard) updateData();
                    }//onComplete

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }


    private void getCard(int idCard) {
        disposGetCardById = cardDao.getCardById(idCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(card -> {
                    tempCard = card;
                    disposGetCardById.dispose();
                    selectAction();
                });
    }//getCard


    private void selectAction(){
        TextView cardNumber = findViewById(R.id.idCardNumber);
        TextView totalAmount = findViewById(R.id.idTotalAmount);
        cardNumber.setText(tempCard.getCardNumber());
        totalAmount.setText(String.format("%s %s", tempCard.getTotalAmount(), this.getResources().getString(R.string.uah)));

        if (flagUpdateCard) {
            switch (selectedAction) {
                case REPLENISH_CARD:
                        tempCard.setTotalAmount(tempCard.getTotalAmount() + history.getAmount());
                        updateCard(tempCard);
                    break;
                case WITHDRAW_MONEY:
                        tempCard.setTotalAmount(tempCard.getTotalAmount() - history.getAmount());
                        updateCard(tempCard);
                    break;
                case TRANSFER_MONEY:
                        tempCard.setTotalAmount(tempCard.getTotalAmount() - history.getAmount());
                        updateCard(tempCard);
                        getListCardsUser();
                    break;
            }//switch
            flagUpdateCard = false;
        }//if
    }//selectAction


    public void getListCardsUser() {
        disposListCardsUser = cardDao.allCardsUser(tempCard.getIdUser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listCards -> {
                    disposListCardsUser.dispose();
                    updateRecipientCard(listCards);
                });
    }//getListCardsUser


    private void updateRecipientCard(List<Card>listCards){
        String numCard = history.getRecipientCard();
        for (int i = 0; i < listCards.size(); i++) {
            if (listCards.get(i).getCardNumber().equals(numCard)) {
                int totalAmount = listCards.get(i).getTotalAmount() + history.getAmount();
                listCards.get(i).setTotalAmount(totalAmount);
                updateCard(listCards.get(i));
                //сделать update истории карты получателя
                int id = listCards.get(i).getId();
                history.setIdSenderCard(id);
                history.setReplenishment(true);
                insertHistory(history);
            }
        }
    }

}//class HistoryActivity
