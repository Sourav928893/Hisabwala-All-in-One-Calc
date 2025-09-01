package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.currency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hisabwalaallinonecalc.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * Adapter to display a list of currencies with flag, symbol, and name.
 */
public class CurrencyAdapter extends ArrayAdapter<Currency> {

    private final boolean showEnglishName;

    public CurrencyAdapter(@NonNull Context context, @NonNull List<Currency> currencies, boolean showEnglishName) {
        super(context, 0, currencies);
        this.showEnglishName = showEnglishName;
    }

    private static class ViewHolder {
        ShapeableImageView flagImage;
        TextView symbolText;
        TextView nameText;
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.currency_layout, parent, false);
            holder = new ViewHolder();
            holder.flagImage = convertView.findViewById(R.id.flag);
            holder.symbolText = convertView.findViewById(R.id.symbol);
            holder.nameText = convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Currency currency = getItem(position);
        if (currency != null) {
            // Set flag image
            holder.flagImage.setImageResource(currency.id());

            // Set currency symbol
            holder.symbolText.setText(currency.symbol());

            // Set currency name (English or Chinese)
            holder.nameText.setText(showEnglishName ? currency.englishName() : currency.chineseName());
        }

        return convertView;
    }
}
