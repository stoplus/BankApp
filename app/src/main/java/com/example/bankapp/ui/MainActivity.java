package com.example.bankapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bankapp.MyApp;
import com.example.bankapp.OnItemListener;
import com.example.bankapp.R;
import com.example.bankapp.adapters.AdapterCard;
import com.example.bankapp.entityRoom.Card;
import com.example.bankapp.entityRoom.CardDao;
import com.example.bankapp.entityRoom.HistoryDao;
import com.example.bankapp.entityRoom.UserDao;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation;

public class MainActivity extends AppCompatActivity {
    @Inject
    CardDao cardDao;
    @Inject
    HistoryDao historyDao;
    @Inject
    UserDao userDao;
    int idUser;
    private Disposable disposListCardsUser;
    private Disposable disposNubbersCard;
    private RecyclerView recyclerView;
    private View view;
    private LinearLayout blocNoCard;
    private LinearLayout blocHead;
    private int positionDelete = -1;
    private AdapterCard adapter;
    private boolean adapterFlag;
    private List<Card> tempListCards;
    private List<String> listAllNumbersCards;
    private final int MAX_RANDOM = 10000;
    public static final String EXTRA_TRANSITION_NAME_CARD = "transition_name_card";
    public static final String EXTRA_TRANSITION_NAME_AMOUNT = "transition_name_amount";

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(view);

        idUser = getIntent().getIntExtra("idUser", 0);
        recyclerView = findViewById(R.id.idRecycler);

        MyApp.app().dataBaseComponent().inject(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.list_cards));
        getListCardsUser(idUser);//get the list of cards
    }//onCreate


    @Override
    protected void onRestart() {
        super.onRestart();
        getListCardsUser(idUser);//get the list of cards
    }//onRestart


    private void setupWidget() {
        blocHead = findViewById(R.id.idBlocHead);
        blocNoCard = findViewById(R.id.idBlocNoCard);
        Button regCardButton = findViewById(R.id.regCardButton);

        if (tempListCards.size() == 0) {
            blocNoCard.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            blocHead.setVisibility(View.GONE);

            regCardButton.setOnClickListener(v -> {
                createNewCard(); //register a new card
            });
        } else showCards();
    }//setupWidget


    private void showCards() {
        blocHead.setVisibility(View.VISIBLE);
        blocNoCard.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        if (adapter == null) {
            adapter = new AdapterCard(this, tempListCards, onItemListener);
            recyclerView.setAdapter(adapter);
        } else {
            if (positionDelete > -1) {
                //delete
                adapter.deleteCardAdapter(positionDelete);
                positionDelete = -1;
                adapterFlag = false;
            } else {
                //add
                if (adapterFlag) {
                    adapter = new AdapterCard(this, tempListCards, onItemListener);
                    recyclerView.setAdapter(adapter);
                }//if
            }//if
        }//if
    }//setCards


    private OnItemListener onItemListener = new OnItemListener() {
        @Override
        public void onItemClick(int position, View v, TextView cardNumber, TextView totalAmount) {
            //open history
            int idCard = tempListCards.get(position).getId();
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra("idCard", idCard);
            intent.putExtra(EXTRA_TRANSITION_NAME_CARD, ViewCompat.getTransitionName(cardNumber));
            intent.putExtra(EXTRA_TRANSITION_NAME_AMOUNT, ViewCompat.getTransitionName(totalAmount));

            Pair<View, String> pCard = Pair.create(cardNumber, ViewCompat.getTransitionName(cardNumber));
            Pair<View, String> pAmount = Pair.create(totalAmount, ViewCompat.getTransitionName(totalAmount));

            ActivityOptionsCompat options =
                    makeSceneTransitionAnimation(MainActivity.this, pCard, pAmount);

            startActivity(intent, options.toBundle());
            adapter = null;
        }//onItemClick

        @Override
        public void onItemLongClick(int position, View v) {
            positionDelete = position;
            PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.CENTER);
            popup.inflate(R.menu.context_menu_card);
            //onMenuItemClick
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.deleteCard:
                        deleteCard(tempListCards.get(position));
                        return true;
                }//switch
                return false;
            });
            popup.show();
        }//onItemLongClick
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }//onCreateOptionsMenu


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.addCard:
                createNewCard();
                break;
            case R.id.exit:
                finish();
                break;
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected


    private void createNewCard() {
        getListAllNumbersCards();
    }//createNewCard


    private void createNumberCard() {
        String pinCode = getRandom();
        String numberCard = generateNumberCard();

        for (int i = 0; i < listAllNumbersCards.size(); i++) {
            if (listAllNumbersCards.get(i).equals(numberCard)) {
                createNumberCard();
            }
        }
        insertCard(new Card(idUser, numberCard, 0, pinCode));
    } //createNumberCard


    private String generateNumberCard() {
        StringBuilder builder = new StringBuilder();
        builder = builder.append(getRandom())
                .append(" ").append(getRandom())
                .append(" ").append(getRandom())
                .append(" ").append(getRandom());
        return builder.toString();
    }//generateNumberCard


    private String getRandom() {
        Random rand = new Random();
        return String.format(Locale.US, "%04d", rand.nextInt(MAX_RANDOM));
    }// getRandom


    public void getListCardsUser(int idUser) {
        disposListCardsUser = cardDao.allCardsUser(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listCards -> {
                    disposListCardsUser.dispose();
                    tempListCards = listCards;
                    setupWidget();
                });
    }//getListCardsUser


    public void getListAllNumbersCards() {
        disposNubbersCard = cardDao.allNumbersCards()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listNumbersCards -> {
                    disposNubbersCard.dispose();
                    listAllNumbersCards = listNumbersCards;
                    createNumberCard();
                });
    }//getListImageObj


    private void insertCard(final Card card) {
        Completable.fromAction(() -> cardDao.insert(card))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        positionDelete = -1;
                        adapterFlag = true;
                        getListCardsUser(idUser);//get the list cards
                        Snackbar.make(view, getResources().getString(R.string.cardAdded), Snackbar.LENGTH_LONG).show();
                    }//onComplete

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }//addProductForList


    private void deleteCard(final Card card) {
        Completable.fromAction(() -> cardDao.delete(card))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        adapterFlag = true;
                        getListCardsUser(idUser);//get the list cards
                        Snackbar.make(view, getResources().getString(R.string.cardDeleted), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }//deleteCard
}//class MainActivity
