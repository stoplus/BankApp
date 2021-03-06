package com.example.bankapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.Objects;

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
    public boolean flagUpdateCard;
    private History history;
    private int selectedAction;
    private TextView pinCode;
    private TextView cardNumber;
    private TextView totalAmount;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setToolBar();
        MyApp.app().dataBaseComponent().inject(this);
        idCard = getIntent().getIntExtra("idCard", 0);

        setTransitionName();
        updateData();
    }//onCreate


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.history_card));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }//setToolBar


    //animation
    private void setTransitionName() {
        String cardTransitionName = getIntent().getStringExtra(MainActivity.EXTRA_TRANSITION_NAME_CARD);
        String amountTransitionName = getIntent().getStringExtra(MainActivity.EXTRA_TRANSITION_NAME_AMOUNT);

        cardNumber = findViewById(R.id.idCardNumber);
        totalAmount = findViewById(R.id.idTotalAmount);
        cardNumber.setTransitionName(cardTransitionName);
        totalAmount.setTransitionName(amountTransitionName);
    }//setTransitionName


    private void updateData() {
        getListHistoryCard(idCard);//get the history of this card
        getCard(idCard);
    }//updateData


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
            case R.id.send_money:
                args.putInt("Select_action", TRANSFER_MONEY);
                break;
            case R.id.withdraw_money://снять
                args.putInt("Select_action", WITHDRAW_MONEY);
                break;
            case R.id.replenish_account://пополнить
                args.putInt("Select_action", REPLENISH_CARD);
                break;
            case android.R.id.home:
                this.supportFinishAfterTransition();
                return true;
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
                    setListHistory(listHistory);
                });
    }//getListCardsUser

    private void setListHistory(List<History> listHistory) {
        RecyclerView recyclerView = findViewById(R.id.idRecyclerHistory);
        TextView textHistory = findViewById(R.id.textHistory);
        if (listHistory.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            textHistory.setVisibility(View.VISIBLE);
        } else {
            textHistory.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            AdapterHistory adapter = new AdapterHistory(this, listHistory);
            recyclerView.setAdapter(adapter);
        }
    }//setListHistory


    @Override
    public void onGetDataFromDialog(int selectedAction, History history) {
        flagUpdateCard = true;
        this.history = history;
        this.selectedAction = selectedAction;
        idCard = history.getIdSenderCard();
        insertHistory(history);
    }//onGetDataFromDialog


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
    }//insertHistory


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
    }//updateCard


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


    private void selectAction() {
        if (pinCode == null) {
            pinCode = findViewById(R.id.idPinCode);
            checkBox = findViewById(R.id.idCheckBoxPinCode);
        }

        pinCode.setText(tempCard.getPinCode());
        checkBox.setOnCheckedChangeListener(listner);// set the listener to the visibility pin-code checkbox
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


    private CompoundButton.OnCheckedChangeListener listner = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                pinCode.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);//hide the password
            } else {
                pinCode.setInputType(129);//show the password
            }//if
        }//onCheckedChanged
    };


    public void getListCardsUser() {
        disposListCardsUser = cardDao.allCardsUser(tempCard.getIdUser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listCards -> {
                    disposListCardsUser.dispose();
                    updateRecipientCard(listCards);
                });
    }//getListCardsUser


    private void updateRecipientCard(List<Card> listCards) {
        String numCard = history.getRecipientCard();
        for (int i = 0; i < listCards.size(); i++) {
            if (listCards.get(i).getCardNumber().equals(numCard)) {
                int totalAmount = listCards.get(i).getTotalAmount() + history.getAmount();
                listCards.get(i).setTotalAmount(totalAmount);
                updateCard(listCards.get(i));
                // do update the history of the recipient's card
                int id = listCards.get(i).getId();
                history.setIdSenderCard(id);
                history.setReplenishment(true);
                insertHistory(history);
            }//if
        }//for
    }//updateRecipientCard

    @Override
    public void onBackPressed() {
        this.supportFinishAfterTransition();
    }
}//class HistoryActivity
