package com.ansoft.speedup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.ansoft.speedup.profiles.ActionEditFragment;
import com.ansoft.speedup.profiles.ConditionEditFragment;
import com.ansoft.speedup.profiles.Profile;
import com.ansoft.speedup.profiles.ProfileSettingsFragment;
import com.github.paolorotolo.appintro.AppIntro;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ansoft.speedup.profiles.action.Action;
import com.ansoft.speedup.profiles.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public class AddProfileActivity extends AppIntro {


    private static final String ACTION_TAG="Action";
    private static final String CONDITION_TAG="Condition";
    private static final String SETTINGS_TAG="Settings";
    Fragment actionEditFragment;
    LinearLayout buttonBack;
    LinearLayout buttonNext;
    Fragment conditionEditFragment;
    private int currentPane;
    ArrayList<Integer> exclusivePriorities;
    FragmentTransaction ft;
    private Gson gson;
    private Profile profile;
    Fragment profileSettingsFragment;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);






        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Condition.class, new Condition.Serializer());
        gsonBuilder.registerTypeAdapter(Condition.class, new Condition.Deserializer());
        gsonBuilder.registerTypeAdapter(Action.class, new Action.Serializer());
        gsonBuilder.registerTypeAdapter(Action.class, new Action.Deserializer());
        gsonBuilder.disableHtmlEscaping();




        this.gson = gsonBuilder.create();
        Bundle extras = getIntent().getExtras();
        String serializedProfile;
        if (extras == null && savedInstanceState == null) {
            this.profile = new Profile();
            this.exclusivePriorities = null;
        } else if (savedInstanceState == null) {
            serializedProfile = extras.getString("profile");
            this.exclusivePriorities = extras.getIntegerArrayList("exclusive");
            try {
                this.profile =this.gson.fromJson(serializedProfile, Profile.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                this.profile = new Profile();
            }
        } else {
            serializedProfile = savedInstanceState.getString("profile");
            this.exclusivePriorities = savedInstanceState.getIntegerArrayList("exclusive");
            try {
                this.profile = this.gson.fromJson(serializedProfile, Profile.class);
            } catch (JsonSyntaxException e2) {
                e2.printStackTrace();
                this.profile = new Profile();
            }
        }
        this.ft = getSupportFragmentManager().beginTransaction();
        this.conditionEditFragment = new ConditionEditFragment();
        this.actionEditFragment = new ActionEditFragment();
        this.profileSettingsFragment = new ProfileSettingsFragment();

        /*
        if (savedInstanceState == null) {
            this.ft.replace(R.id.fragmentContainer, this.conditionEditFragment, CONDITION_TAG);
        } else {
            int pane = savedInstanceState.getInt("currentPane");
            if (pane == 0) {
                this.ft.replace(R.id.fragmentContainer, this.conditionEditFragment, CONDITION_TAG);
            }
            if (pane == 1) {
                this.ft.replace(R.id.fragmentContainer, this.actionEditFragment, ACTION_TAG);
            } else if (pane == 2) {
                this.ft.replace(R.id.fragmentContainer, this.profileSettingsFragment, SETTINGS_TAG);
            }
            this.currentPane = pane;
        }
        this.ft.commit();
        this.buttonNext = (LinearLayout) findViewById(R.id.buttonNext);
        this.buttonBack = (LinearLayout) findViewById(R.id.buttonBack);
*/

        addSlide(this.conditionEditFragment);
        addSlide(this.actionEditFragment);
        addSlide(this.profileSettingsFragment);


        //addSlide(AppIntroFragment.newInstance("Intro", "dd", R.drawable.ic_menn, getResources().getColor(R.color.colorFAB3)));
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        //addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.

        setSeparatorColor(getResources().getColor(android.R.color.transparent));
        setNextArrowColor(getResources().getColor(R.color.colorPrimary));
        setIndicatorColor(getResources().getColor(R.color.colorPrimary), Color.parseColor("#80F44336"));
        setColorDoneText(getResources().getColor(R.color.colorPrimary));
        setDoneText("Save");

        setDepthAnimation();
        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("D66054767372CF1F16E35ABDBCF25CE6")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        ((ProfileSettingsFragment) this.profileSettingsFragment).save();
        Intent result = new Intent();
        result.putExtra("profile", this.gson.toJson(this.profile));
        setResult(0, result);
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }



    public Condition getBaseCondition() {

        if (this.profile!=null){
            return this.profile.getBaseCondition();
        }else{
            return new Condition();
        }
    }

    public List<Action> getActionList() {
        return this.profile.getActionList();
    }

    public Profile getProfile() {
        return this.profile;
    }

    public ArrayList<Integer> getExclusivePriorities() {
        return this.exclusivePriorities;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Gson getGson() {
        return this.gson;
    }
}
