package com.magister.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubscriptionActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private static final String SKU_WEEKLY = "one_week";
    private static final String SKU_MONTHLY = "one_month";
    private static final String SKU_ANNUAL = "tutorfinder-annual1";

    private BillingClient billingClient;
    private CardView weeklySubscription;
    private CardView monthlySubscription;
    private CardView annualSubscription;
    private ProgressBar progressBar;

    private String selectedSku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        weeklySubscription = findViewById(R.id.card_weekly_subscription);
        monthlySubscription = findViewById(R.id.card_monthly_subscription);
        annualSubscription = findViewById(R.id.card_annual_subscription);
        progressBar = findViewById(R.id.progressBar);

        setupBillingClient();

        weeklySubscription.setOnClickListener(v -> initiatePurchase(SKU_WEEKLY));
        monthlySubscription.setOnClickListener(v -> initiatePurchase(SKU_MONTHLY));
        annualSubscription.setOnClickListener(v -> initiatePurchase(SKU_ANNUAL));
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        connectToPlayBillingService();
    }

    private void connectToPlayBillingService() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    querySkuDetails();
                    queryPurchases();
                } else {
                    showToast("Billing setup failed.");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });
    }

    private void querySkuDetails() {
        List<String> skuList = new ArrayList<>();
        skuList.add(SKU_WEEKLY);
        skuList.add(SKU_MONTHLY);
        skuList.add(SKU_ANNUAL);

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    logSubscriptionDetails(skuDetails);
                }
            } else {
                showToast("Failed to retrieve SKU details.");
            }
        });
    }

    private void queryPurchases() {
        Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
        if (purchasesResult.getPurchasesList() != null) {
            for (Purchase purchase : purchasesResult.getPurchasesList()) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    launchDashboard();
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BillingClient.BillingResponseCode.OK && resultCode == RESULT_OK) {
            queryPurchases();
        } else {
            showToast("Purchase failed.");
        }
    }

    private void initiatePurchase(String sku) {
        progressBar.setVisibility(View.VISIBLE);

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(Collections.singletonList(sku))
                .setType(BillingClient.SkuType.SUBS)
                .build();

        billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                SkuDetails skuDetails = skuDetailsList.get(0);
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                BillingResult responseCode = billingClient.launchBillingFlow(this, flowParams);
                if (responseCode.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    showToast("Unable to initiate purchase.");
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                showToast("Failed to retrieve SKU details.");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    launchDashboard();
                    return;
                }
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    private void launchDashboard() {
        Intent intent = new Intent(this, Final_dashboard.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void logSubscriptionDetails(SkuDetails skuDetails) {
        Log.d("SubscriptionActivity", "SKU: " + skuDetails.getSku());
        Log.d("SubscriptionActivity", "Title: " + skuDetails.getTitle());
        Log.d("SubscriptionActivity", "Price: " + skuDetails.getPrice());
    }
}
