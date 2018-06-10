package com.example.bankapp.ui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.bankapp.MyApp;
import com.example.bankapp.OnItemListener;
import com.example.bankapp.R;
import com.example.bankapp.adapters.AdapterCard;
import com.example.bankapp.entityRoom.Card;
import com.example.bankapp.entityRoom.CardDao;
import com.example.bankapp.entityRoom.HistoryDao;
import com.example.bankapp.entityRoom.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @Inject
    CardDao cardDao;
    @Inject
    HistoryDao historyDao;
    @Inject
    UserDao userDao;
    int idUser;
    private Disposable dispos;
    private Disposable disposNubbersCard;
    private RecyclerView recyclerView;
    private View view;
    private LinearLayout bloc;
    private int positionDelete = -1;
    private AdapterCard adapter;
    private boolean adapterFlag;
    private List<Card> tempListCards;
    private List<String> listAllNumbersCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(view);

        idUser = getIntent().getIntExtra("idUser", 0);

        recyclerView = findViewById(R.id.idRecycler);

        MyApp.app().dataBaseComponent().inject(this);

        getListCardsUser(idUser);//получаем список карт
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getListCardsUser(idUser);//получаем список карт
    }

    private void setupWidget() {
        bloc = findViewById(R.id.idBloc);
        Button regCardButton = findViewById(R.id.regCardButton);

        if (tempListCards.size() == 0) {
            bloc.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            regCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewCard(); //регистрируем новую карту
                }
            });
        } else {
            setCards();
        }
    }

    private void setCards() {
        bloc.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        OnItemListener onItemListener = new OnItemListener() {
            @Override
            public void onItemClick(int position, View v) {
                //открываем историю
                int idCard = tempListCards.get(position).getId();
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                intent.putExtra("idCard", idCard);
                startActivity(intent);
                adapter= null;
            }

            @Override
            public void onItemLongClick(int position, View v) {
                positionDelete = position;
                PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.CENTER);
                popup.inflate(R.menu.context_menu_card);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.deleteCard:
                                deleteCard(tempListCards.get(position));
                                return true;
                        }//switch
                        return false;
                    }//onMenuItemClick
                });
                popup.show();
            }
        };

        if (adapter == null) {
            adapter = new AdapterCard(this, tempListCards, onItemListener);
            recyclerView.setAdapter(adapter);
        } else {
            if (positionDelete > -1) {
                //удаляем
                adapter.deleteCardAdapter(positionDelete);
                positionDelete = -1;
                adapterFlag = false;
            } else {
                //добавляем
                if (adapterFlag) {
                    adapter = new AdapterCard(this, tempListCards, onItemListener);
                    recyclerView.setAdapter(adapter);
                }
            }
        }//if

    }//setCards


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
    }


    private String getRandom() {
        Random rand = new Random();
        return String.format(Locale.US, "%04d", rand.nextInt(10000));
    }// getRandom


    public void getListCardsUser(int idUser) {
        dispos = cardDao.allCardsUser(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listCards -> {
                    dispos.dispose();
                    tempListCards = listCards;
                    setupWidget();
                });
    }//getListCardsUser


    public void getListAllNumbersCards() {
        dispos = cardDao.allNumbersCards()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listNumbersCards -> {
                    dispos.dispose();
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
                        getListCardsUser(idUser);//получаем список карт
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
                        getListCardsUser(idUser);//получаем список карт
                        Snackbar.make(view, getResources().getString(R.string.cardDeleted), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }
}
