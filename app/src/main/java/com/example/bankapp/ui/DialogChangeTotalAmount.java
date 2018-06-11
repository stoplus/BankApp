package com.example.bankapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.bankapp.MyApp;
import com.example.bankapp.R;
import com.example.bankapp.ForDialog;
import com.example.bankapp.adapters.AdapterSpinner;
import com.example.bankapp.entityRoom.Card;
import com.example.bankapp.entityRoom.CardDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.bankapp.ui.HistoryActivity.REPLENISH_CARD;
import static com.example.bankapp.ui.HistoryActivity.TRANSFER_MONEY;
import static com.example.bankapp.ui.HistoryActivity.WITHDRAW_MONEY;

public class DialogChangeTotalAmount extends DialogFragment {
    @Inject
    CardDao cardDao;
    private ForDialog datable;
    private Spinner spinnerWhere;
    private Spinner spinnerWherefrom;
    private EditText editTextCardRecipient;
    private int idUser;
    private Disposable disposNubbersCard;
    private Disposable disposCardByNumberCard;
    private DialogInterface dialogTemp;
    private Card selectedCartSender;
    private EditText enterAmount;

    @Override // Метод onAttach() вызывается в начале жизненного цикла фрагмента
    public void onAttach(Context context) {
        super.onAttach(context);
        datable = (ForDialog) context;
    } // onAttach

    @NonNull // создание диалога
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity current = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(current);
        MyApp.app().dataBaseComponent().inject(this);

        //создаем вид
        View view = Objects.requireNonNull(current).getLayoutInflater().inflate(R.layout.dialog_change_total_amount, null);

        TextView cardWherefrom = view.findViewById(R.id.textViewWherefrom);
        TextView cardWhere = view.findViewById(R.id.textViewWhere);
        editTextCardRecipient = view.findViewById(R.id.idCardRecipient);
        enterAmount = view.findViewById(R.id.idEnterAmount);
        spinnerWherefrom = view.findViewById(R.id.spinnerWherefrom);
        spinnerWhere = view.findViewById(R.id.spinnerWhere);

        idUser = Objects.requireNonNull(getArguments()).getInt("idUser");
        String title = "";
        switch (Objects.requireNonNull(getArguments()).getInt("Select_action")) {
            case REPLENISH_CARD:
                title = "Пополнить карту";
                cardWherefrom.setVisibility(View.GONE);
                cardWhere.setVisibility(View.GONE);
                spinnerWhere.setVisibility(View.GONE);
                break;
            case WITHDRAW_MONEY:
                title = "Снять деньги";
                cardWherefrom.setVisibility(View.GONE);
                cardWhere.setVisibility(View.GONE);
                spinnerWhere.setVisibility(View.GONE);
                break;
            case TRANSFER_MONEY:
                title = "Перевод между картами";
                break;
        }//switch

        getCardsUser();//получаем список карт пользователя

        builder.setTitle(title)
//                .setIcon(R.drawable.hplib_img_btn_playlist)
                .setView(view);//показываем созданный вид

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                dialogTemp = dialog;
                Button buttonCancel = getDialog().findViewById(R.id.buttonCancel);
                Button buttonOk = getDialog().findViewById(R.id.buttonOk);

                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectAction(v);
                    }
                });

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;
    } // onCreateDialog


    private void selectAction(View view){
        String amount = enterAmount.getText().toString();
        if (!amount.isEmpty()) {
            int enterAmountSum = Integer.parseInt(amount);
            Date dateNow = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            String date = formatForDateNow.format(dateNow);

            String numCard = ((AdapterSpinner) spinnerWherefrom.getAdapter())
                    .list.get(spinnerWherefrom.getSelectedItemPosition()).getCardNumber();

            switch (Objects.requireNonNull(getArguments()).getInt("Select_action")) {
                case REPLENISH_CARD://Пополнить карту
                    replenish(view, date, numCard, enterAmountSum);
                    break;
                case WITHDRAW_MONEY://Снять деньги
                    withdraw(view, date, numCard, enterAmountSum);
                    break;
                case TRANSFER_MONEY://Перевод между картами
                    transfer(view, date, numCard, enterAmountSum);
                    break;
            }
        } else
            Snackbar.make(view, "Введите сумму!", Snackbar.LENGTH_SHORT).show();
    }


    private void replenish(View view, String date, String numCard, int enterAmountSum) {
        //отправляем данные в активность с историей
        datable.onGetDataFromDialog(date,
                enterAmountSum,
                selectedCartSender.getId(),
                numCard, REPLENISH_CARD, true);
        dialogTemp.dismiss();
    }//replenish


    private void withdraw(View view, String date, String numCard, int enterAmountSum) {
        if (selectedCartSender.getTotalAmount() >= enterAmountSum) {
            //отправляем данные в активность с историей
            datable.onGetDataFromDialog(date,
                    enterAmountSum,
                    selectedCartSender.getId(),
                    numCard, WITHDRAW_MONEY, false);
            dialogTemp.dismiss();
        } else Snackbar.make(view, "Недостаточно средств!", Snackbar.LENGTH_SHORT).show();
    }//replenish


    private void transfer(View view, String date, String numCardSender, int enterAmountSum) {
        String numCardRecipient = ((AdapterSpinner) spinnerWhere.getAdapter())
                .list.get(spinnerWhere.getSelectedItemPosition()).getCardNumber();

        if (!numCardRecipient.equals(numCardSender)) {
            if (selectedCartSender.getTotalAmount() >= enterAmountSum) {
                //отправляем данные в активность с историей
                datable.onGetDataFromDialog(date,
                        enterAmountSum,
                        selectedCartSender.getId(),
                        numCardRecipient, TRANSFER_MONEY, false);
                dialogTemp.dismiss();
            } else Snackbar.make(view, "Недостаточно средств!", Snackbar.LENGTH_SHORT).show();
        } else Snackbar.make(view, "Вы не можите указывать одну и туже карту!",
                Snackbar.LENGTH_SHORT).show();
    }//transfer


    //слушатель включения окна ручного ввода карты
    private AdapterView.OnItemSelectedListener itemSelectedListenerWhere = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String numCard = ((AdapterSpinner) spinnerWhere.getAdapter())
                    .list.get(spinnerWhere.getSelectedItemPosition()).getCardNumber();

            if (numCard.equals("Ручной ввод")) {//сравниваем имя в спинере со значением
                spinnerWhere.setVisibility(View.GONE);
                editTextCardRecipient.setVisibility(View.VISIBLE);
            } else {
                spinnerWhere.setVisibility(View.VISIBLE);
                editTextCardRecipient.setVisibility(View.GONE);
            }//if
        }//itemSelected

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }//авто генерируемый метод
    };


    //слушатель изменения карты отправителя
    private AdapterView.OnItemSelectedListener itemSelectedListenerWherefrom = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String numCard = ((AdapterSpinner) spinnerWherefrom.getAdapter())
                    .list.get(spinnerWherefrom.getSelectedItemPosition()).getCardNumber();
            getCardByNumberCard(numCard);
        }//itemSelected

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }//авто генерируемый метод
    };


    //получаем список карт пользователя
    private void getCardsUser() {
        disposNubbersCard = cardDao.allCardsUser(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listCardsUser -> {
                    disposNubbersCard.dispose();
                    FragmentActivity current = Objects.requireNonNull(getActivity());

                    //спинер с какой карты
                    AdapterSpinner adapterWherefrom = new AdapterSpinner(current, R.layout.spiner, listCardsUser);
                    spinnerWherefrom.setAdapter(adapterWherefrom);//устанавливаем адаптер в спинер
                    spinnerWherefrom.setSelection(0); // устанавливаем значение по умолчанию
                    spinnerWherefrom.setOnItemSelectedListener(itemSelectedListenerWherefrom);//устанавливаем слушатель на спинер

                    if (Objects.requireNonNull(getArguments()).getInt("Select_action") == TRANSFER_MONEY) {
                        //спинер на какую карту
                        List<Card> newListNumbersCard = new ArrayList<>(listCardsUser);
                        newListNumbersCard.add(new Card(0, "Ручной ввод", 0, ""));
                        AdapterSpinner adapterWhere = new AdapterSpinner(current, R.layout.spiner, newListNumbersCard);
                        spinnerWhere.setAdapter(adapterWhere);//устанавливаем адаптер в спинер
                        spinnerWhere.setSelection(0); // устанавливаем значение по умолчанию
                        spinnerWhere.setOnItemSelectedListener(itemSelectedListenerWhere);//устанавливаем слушатель на спинер

                        //получаем карту по ее номеру
                        String numCard = newListNumbersCard.get(spinnerWherefrom.getSelectedItemPosition()).getCardNumber();
                        getCardByNumberCard(numCard);
                    }

                    if (Objects.requireNonNull(getArguments()).getInt("Select_action") == REPLENISH_CARD) {
                        //получаем карту по ее номеру
                        String numCard = listCardsUser.get(spinnerWherefrom.getSelectedItemPosition()).getCardNumber();
                        getCardByNumberCard(numCard);
                    }
                });
    }//getNumbersCard


    private void getCardByNumberCard(String numCard) {
        disposCardByNumberCard = cardDao.getCardByNumberCard(numCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(card -> {
                    disposCardByNumberCard.dispose();
                    selectedCartSender = card;
                });
    }//getCardByNumberCard
} // class DialogPlayList
