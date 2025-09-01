package com.example.hisabwalaallinonecalc.main.toolbox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.bmi.BMIActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.chinese.ChineseNumberConversionActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.compass.Compass;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.converter.UnitActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.currency.CurrencyActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.equation.EquationActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.finance.FinanceActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.fraction.FractionActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.programmer.ProgrammerActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.random.RandomNumberActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.relationship.RelationshipActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.ruler.RulerActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.shopping.ShoppingActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.StatisticsActivity;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.time.DateRangeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolBoxFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private ToolBoxAdapter adapter;
    private List<ToolBoxItem> newData;
    private List<ToolBoxItem> data;
    private boolean isGrid;

    public ToolBoxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = createToolBoxItems();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private List<ToolBoxItem> createToolBoxItems() {
        List<ToolBoxItem> items = new ArrayList<>();
        items.add(new ToolBoxItem(Constants.UNIT_ACTIVITY_ID, getString(R.string.UnitsActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.unit_icon)));
        items.add(new ToolBoxItem(Constants.DATE_RANGE_ACTIVITY_ID, getString(R.string.dateActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.date_range_icon)));
        items.add(new ToolBoxItem(Constants.FINANCE_ACTIVITY_ID, getString(R.string.financeActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.finance_icon)));
        items.add(new ToolBoxItem(Constants.COMPASS_ACTIVITY_ID, getString(R.string.compassActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.compass_icon)));
        items.add(new ToolBoxItem(Constants.BMI_ACTIVITY_ID, getString(R.string.bmiActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.bmi_icon)));
        items.add(new ToolBoxItem(Constants.SHOPPING_ACTIVITY_ID, getString(R.string.shoppingActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.shopping_icon)));
        items.add(new ToolBoxItem(Constants.CURRENCY_ACTIVITY_ID, getString(R.string.exchangeActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.currency_exchange_icon)));
        items.add(new ToolBoxItem(Constants.CHINESE_NUMBER_CONVERSION_ACTIVITY_ID, getString(R.string.chineseNumberConverter), AppCompatResources.getDrawable(requireContext(), R.drawable.chinese_number_icon)));
        items.add(new ToolBoxItem(Constants.RELATIONSHIP_ACTIVITY_ID, getString(R.string.relationshipActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.relation_icon)));
        items.add(new ToolBoxItem(Constants.RANDOM_ACTIVITY_ID, getString(R.string.randomActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.random_number_icon)));
        items.add(new ToolBoxItem(Constants.FUNCTION_ACTIVITY_ID, getString(R.string.EquationActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.functions_icon)));
        items.add(new ToolBoxItem(Constants.STATISTICS_ACTIVITY_ID, getString(R.string.statisticActivity), AppCompatResources.getDrawable(requireContext(), R.drawable.statistics_icon)));
        items.add(new ToolBoxItem(Constants.FRACTION_ACTIVITY_ID, getString(R.string.numberConvert), AppCompatResources.getDrawable(requireContext(), R.drawable.fraction)));
        items.add(new ToolBoxItem(Constants.PROGRAMMER_ACTIVITY_ID, getString(R.string.programmer), AppCompatResources.getDrawable(requireContext(), R.drawable.binary_icon)));
        items.add(new ToolBoxItem(Constants.RULER_ACTIVITY_ID, getString(R.string.ruler), AppCompatResources.getDrawable(requireContext(), R.drawable.ruler_icon)));
        return items;
    }

    public static ToolBoxFragment newInstance() {
        return new ToolBoxFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toolbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View notes, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(notes, savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        isGrid = sharedPreferences.getBoolean("GridLayout", true);

        getParentFragmentManager().setFragmentResultListener("ChangeLayout", getViewLifecycleOwner(), (requestKey, bundle) -> {
            isGrid = bundle.getBoolean("GridLayout", true);
            updateRecycleView(isGrid);
        });

        // Restore saved order
        String order = sharedPreferences.getString("order", Constants.ORDER);
        List<String> orderList = new ArrayList<>(Arrays.asList(order.split("/")));

        // Fix if order length mismatch
        if (orderList.size() != data.size()) {
            orderList.clear();
            for (int i = 0; i < data.size(); i++) {
                orderList.add(String.valueOf(i));
            }
            saveOrder(orderList);
        }

        newData = new ArrayList<>();
        for (String c : orderList) {
            try {
                int index = Integer.parseInt(c);
                if (index >= 0 && index < data.size()) {
                    newData.add(data.get(index));
                }
            } catch (NumberFormatException ignored) { }
        }

        recyclerView = requireView().findViewById(R.id.recyclerView);
        updateRecycleView(isGrid);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private ItemTouchHelper.Callback createItemTouchHelperCallback() {
        return new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();

                if (fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION) {
                    return false;
                }

                ToolBoxItem movedToolBoxItem = newData.remove(fromPosition);
                newData.add(toPosition, movedToolBoxItem);
                adapter.notifyItemMoved(fromPosition, toPosition);

                List<String> newOrder = new ArrayList<>();
                for (ToolBoxItem item : newData) {
                    newOrder.add(String.valueOf(data.indexOf(item)));
                }
                saveOrder(newOrder);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // no-op (no swipe support)
            }
        };
    }

    private void saveOrder(List<String> orderList) {
        String orderString = TextUtils.join("/", orderList);
        sharedPreferences.edit().putString("order", orderString).apply();
    }

    private void updateRecycleView(boolean isGrid) {
        adapter = new ToolBoxAdapter(newData, isGrid, this::startActivityById);
        recyclerView.setLayoutManager(isGrid
                ? new GridLayoutManager(getContext(), 3)
                : new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void startActivityById(ToolBoxItem item) {
        Intent intent = null;
        switch (item.id()) {
            case Constants.UNIT_ACTIVITY_ID:
                intent = new Intent(getContext(), UnitActivity.class);
                break;
            case Constants.DATE_RANGE_ACTIVITY_ID:
                intent = new Intent(getContext(), DateRangeActivity.class);
                break;
            case Constants.FINANCE_ACTIVITY_ID:
                intent = new Intent(getContext(), FinanceActivity.class);
                break;
            case Constants.COMPASS_ACTIVITY_ID:
                intent = new Intent(getContext(), Compass.class);
                break;
            case Constants.BMI_ACTIVITY_ID:
                intent = new Intent(getContext(), BMIActivity.class);
                break;
            case Constants.SHOPPING_ACTIVITY_ID:
                intent = new Intent(getContext(), ShoppingActivity.class);
                break;
            case Constants.CURRENCY_ACTIVITY_ID:
                intent = new Intent(getContext(), CurrencyActivity.class);
                break;
            case Constants.CHINESE_NUMBER_CONVERSION_ACTIVITY_ID:
                intent = new Intent(getContext(), ChineseNumberConversionActivity.class);
                break;
            case Constants.RELATIONSHIP_ACTIVITY_ID:
                intent = new Intent(getContext(), RelationshipActivity.class);
                break;
            case Constants.RANDOM_ACTIVITY_ID:
                intent = new Intent(getContext(), RandomNumberActivity.class);
                break;
            case Constants.FUNCTION_ACTIVITY_ID:
                intent = new Intent(getContext(), EquationActivity.class);
                break;
            case Constants.STATISTICS_ACTIVITY_ID:
                intent = new Intent(getContext(), StatisticsActivity.class);
                break;
            case Constants.FRACTION_ACTIVITY_ID:
                intent = new Intent(getContext(), FractionActivity.class);
                break;
            case Constants.PROGRAMMER_ACTIVITY_ID:
                intent = new Intent(getContext(), ProgrammerActivity.class);
                break;
            case Constants.RULER_ACTIVITY_ID:
                intent = new Intent(getContext(), RulerActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);
    }


    private static class Constants {
        public static final String ORDER = "0/1/2/3/4/5/6/7/8/9/10/11/12/13/14";
        public static final int UNIT_ACTIVITY_ID = 0;
        public static final int DATE_RANGE_ACTIVITY_ID = 1;
        public static final int FINANCE_ACTIVITY_ID = 2;
        public static final int COMPASS_ACTIVITY_ID = 3;
        public static final int BMI_ACTIVITY_ID = 4;
        public static final int SHOPPING_ACTIVITY_ID = 5;
        public static final int CURRENCY_ACTIVITY_ID = 6;
        public static final int CHINESE_NUMBER_CONVERSION_ACTIVITY_ID = 7;
        public static final int RELATIONSHIP_ACTIVITY_ID = 8;
        public static final int RANDOM_ACTIVITY_ID = 9;
        public static final int FUNCTION_ACTIVITY_ID = 10;
        public static final int STATISTICS_ACTIVITY_ID = 11;
        public static final int FRACTION_ACTIVITY_ID = 12;
        public static final int PROGRAMMER_ACTIVITY_ID = 13;
        public static final int RULER_ACTIVITY_ID = 14;
    }
}
