package com.example.bankapp.ui;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bankapp.MyApp;
import com.example.bankapp.R;
import com.example.bankapp.ForDialog;
import com.example.bankapp.adapters.AdapterSpinner;
import com.example.bankapp.entityRoom.Card;
import com.example.bankapp.entityRoom.CardDao;
import com.example.bankapp.entityRoom.History;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private TextView pinCode;
    private ImageView closeManualInput;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        datable = (ForDialog) context;
    } // onAttach

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity current = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(current);
        MyApp.app().dataBaseComponent().inject(this);

        @SuppressLint("InflateParams")
        View view = Objects.requireNonNull(current)
                .getLayoutInflater().inflate(R.layout.dialog_change_total_amount, null);

        TextView cardWherefrom = view.findViewById(R.id.textViewWherefrom);
        TextView cardWhere = view.findViewById(R.id.textViewWhere);
        pinCode = view.findViewById(R.id.idEnterPin);
        editTextCardRecipient = view.findViewById(R.id.idCardRecipient);
        enterAmount = view.findViewById(R.id.idEnterAmount);
        spinnerWherefrom = view.findViewById(R.id.spinnerWherefrom);
        spinnerWhere = view.findViewById(R.id.spinnerWhere);
        closeManualInput = view.findViewById(R.id.idClose);

        idUser = Objects.requireNonNull(getArguments()).getInt("idUser");
        String title = "";
        int image = 0;
        switch (Objects.requireNonNull(getArguments()).getInt("Select_action")) {
            case REPLENISH_CARD:
                image = R.mipmap.put_money;
                title = getResources().getString(R.string.replenish);
                cardWherefrom.setVisibility(View.GONE);
                cardWhere.setVisibility(View.GONE);
                spinnerWhere.setVisibility(View.GONE);
                break;
            case WITHDRAW_MONEY:
                image = R.mipmap.get_money;
                title = getResources().getString(R.string.withdraw);
                cardWherefrom.setVisibility(View.GONE);
                cardWhere.setVisibility(View.GONE);
                spinnerWhere.setVisibility(View.GONE);
                break;
            case TRANSFER_MONEY:
                image = R.mipmap.transfer;
                title = getResources().getString(R.string.transfer);
                break;
        }//switch

        getCardsUser();// get the list of user cards

        builder.setTitle(title)
                .setIcon(image)
                .setView(view);

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


    private void selectAction(View view) {
        String amount = enterAmount.getText().toString();
        String pin = pinCode.getText().toString();
        if (!amount.isEmpty()) {
            if (!pin.isEmpty()) {
                if (pin.equals(selectedCartSender.getPinCode())) {
                    int enterAmountSum = Integer.parseInt(amount);

                    Date dateNow = new Date();
                    SimpleDateFormat formatForDateNow = new SimpleDateFormat(
                            "dd.MM.yyyy hh:mm", Locale.US);
                    String date = formatForDateNow.format(dateNow);

                    String numCard = ((AdapterSpinner) spinnerWherefrom.getAdapter())
                            .list.get(spinnerWherefrom.getSelectedItemPosition()).getCardNumber();

                    History history = new History(date, enterAmountSum, selectedCartSender.getId(),
                            numCard, false);

                    switch (Objects.requireNonNull(getArguments()).getInt("Select_action")) {
                        case REPLENISH_CARD://Пополнить карту
                            replenish(view, history);
                            break;
                        case WITHDRAW_MONEY://Снять деньги
                            withdraw(view, history);
                            break;
                        case TRANSFER_MONEY:
                            transfer(view, history);
                            break;
                    }
                } else
                    Snackbar.make(view, getResources().getString(R.string.wrong_pin), Snackbar.LENGTH_SHORT).show();
            } else
                Snackbar.make(view, getResources().getString(R.string.enter_pin), Snackbar.LENGTH_SHORT).show();
        } else
            Snackbar.make(view, getResources().getString(R.string.enter_amount), Snackbar.LENGTH_SHORT).show();
    }//selectAction


    private void replenish(View view, History history) {
        // send the data to the activity with the history
        history.setReplenishment(true);
        datable.onGetDataFromDialog(REPLENISH_CARD, history);
        dialogTemp.dismiss();
    }//replenish


    private void withdraw(View view, History history) {
        if (selectedCartSender.getTotalAmount() >= history.getAmount()) {
            // send the data to the activity with the history
            datable.onGetDataFromDialog(WITHDRAW_MONEY, history);
            dialogTemp.dismiss();
        } else
            Snackbar.make(view, getResources().getString(R.string.insufficient_funds), Snackbar.LENGTH_SHORT).show();
    }//replenish


    private void transfer(View view, History history) {
        String numCardRecipient = ((AdapterSpinner) spinnerWhere.getAdapter())
                .list.get(spinnerWhere.getSelectedItemPosition()).getCardNumber();

        if (!numCardRecipient.equals(history.getRecipientCard())) {
            if (selectedCartSender.getTotalAmount() >= history.getAmount()) {
                // send the data to the activity with the history
                history.setRecipientCard(numCardRecipient);
                datable.onGetDataFromDialog(TRANSFER_MONEY, history);
                dialogTemp.dismiss();
            } else
                Snackbar.make(view, getResources().getString(R.string.insufficient_funds), Snackbar.LENGTH_SHORT).show();
        } else Snackbar.make(view, getResources().getString(R.string.change_card),
                Snackbar.LENGTH_SHORT).show();
    }//transfer


    //Listener spinner
    private AdapterView.OnItemSelectedListener itemSelectedListenerWhere =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String numCard = ((AdapterSpinner) spinnerWhere.getAdapter())
                            .list.get(spinnerWhere.getSelectedItemPosition()).getCardNumber();

                    if (numCard.equals(getResources().getString(R.string.manual_input))) {//сравниваем имя в спинере со значением
                        setManualInput(false);
                        closeManualInput.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setManualInput(true);
                                spinnerWhere.setSelection(0);// set the default value
                            }
                        });
                    } else {
                        setManualInput(true);
                    }//if
                }//itemSelected

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }//auto-generated method
            };

    private void setManualInput(boolean flag) {
        spinnerWhere.setVisibility(flag ? View.VISIBLE : View.GONE);
        editTextCardRecipient.setVisibility(flag ? View.GONE : View.VISIBLE);
        closeManualInput.setVisibility(flag ? View.GONE : View.VISIBLE);
    }


    // listener changing the sender's card
    private AdapterView.OnItemSelectedListener itemSelectedListenerWherefrom =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String numCard = ((AdapterSpinner) spinnerWherefrom.getAdapter())
                            .list.get(spinnerWherefrom.getSelectedItemPosition()).getCardNumber();
                    getCardByNumberCard(numCard);
                }//itemSelected

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }//auto-generated method
            };


    // get the list of user cards
    private void getCardsUser() {
        disposNubbersCard = cardDao.allCardsUser(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listCardsUser -> {
                    disposNubbersCard.dispose();
                    setupSpinner(listCardsUser);
                });
    }//getNumbersCard


    private void setupSpinner(List<Card> listCardsUser) {
        FragmentActivity current = Objects.requireNonNull(getActivity());

        // Spin from which card
        AdapterSpinner adapterWherefrom = new AdapterSpinner(current, R.layout.spiner, listCardsUser);
        spinnerWherefrom.setAdapter(adapterWherefrom);// install the adapter in the spinner
        spinnerWherefrom.setSelection(0);// set the default value
        spinnerWherefrom.setOnItemSelectedListener(itemSelectedListenerWherefrom);// set the listener on the spinner

        if (Objects.requireNonNull(getArguments()).getInt("Select_action") == TRANSFER_MONEY) {
            // Spiner on which card
            List<Card> newListNumbersCard = new ArrayList<>(listCardsUser);
            newListNumbersCard.add(new Card(0,
                    getResources().getString(R.string.manual_input), 0, ""));
            AdapterSpinner adapterWhere = new AdapterSpinner(current, R.layout.spiner, newListNumbersCard);
            spinnerWhere.setAdapter(adapterWhere);// install the adapter in the spinner
            spinnerWhere.setSelection(0);// set the default value
            spinnerWhere.setOnItemSelectedListener(itemSelectedListenerWhere);// set the listener on the spinner
        }//if
        // get the card by its number
        String numCard = listCardsUser.get(spinnerWherefrom.getSelectedItemPosition()).getCardNumber();
        getCardByNumberCard(numCard);
    }//setupSpinner


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
