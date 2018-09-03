package com.ansoft.speedup.fragment;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ansoft.speedup.AddProfileActivity;
import com.ansoft.speedup.MainActivity;
import com.ansoft.speedup.R;
import com.ansoft.speedup.util.SmoothCheckBox;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.ansoft.speedup.DBHelper;
import com.ansoft.speedup.ProfileConverter;
import com.ansoft.speedup.profiles.Profile;
import com.ansoft.speedup.profiles.ProfileList;
import com.ansoft.speedup.profiles.ProfilesService;
import com.ansoft.speedup.profiles.action.Action;
import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.Condition.Deserializer;
import com.ansoft.speedup.profiles.condition.Condition.Serializer;
import com.ansoft.speedup.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProfilesFragment extends Fragment implements PageFragment {
    public static final String PROFILES_DIR = "profiles";
    public static final String PROFILES_FILE = "profiles.SpeedUp.json";
    Activity activity;
    private ProfileAdapter adapter;
    Context context;
    Profile editedProfile;
    Editor editor;
    SmoothCheckBox enable;
    Gson gson;
    Gson gsonExport;
    private ProfileList profileList;
    File profiles;
    Intent serviceIntent;
    SharedPreferences settings;
    View view;
    FloatingActionButton mainFab, fab1, fab2, fab3;
    boolean isOpen = false;

    /* renamed from: com.ansoft.setthecpu.fragment.ProfilesFragment.2 */
    class C01042 implements OnClickListener {
        C01042() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    /* renamed from: com.ansoft.setthecpu.fragment.ProfilesFragment.3 */
    class C01053 implements OnClickListener {
        final /* synthetic */ EditText val$input;

        C01053(EditText editText) {
            this.val$input = editText;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            String name = this.val$input.getText().toString().trim();
            if (name.contains("/") || name.contains("\u0000")) {

            } else if (name.length() <= 0) {
                ProfilesFragment.this.backup("profiles_" + Long.toHexString(System.currentTimeMillis()) + ".SpeedUp.json");
            } else {
                if (!name.endsWith(".SpeedUp.json")) {
                    name = name + ".SpeedUp.json";
                }
                ProfilesFragment.this.backup(name);
            }
        }
    }

    /* renamed from: com.ansoft.setthecpu.fragment.ProfilesFragment.4 */
    class C01064 implements OnClickListener {
        final /* synthetic */ String[] val$items;

        C01064(String[] strArr) {
            this.val$items = strArr;
        }

        public void onClick(DialogInterface dialog, int item) {
            ProfilesFragment.this.restore(this.val$items[item]);
        }
    }

    private class ProfileAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private ProfileList profileList;

        class C01082 implements View.OnClickListener {
            final /* synthetic */ Profile val$profile;

            C01082(Profile profile) {
                this.val$profile = profile;
            }

            public void onClick(View v) {
                Intent intent = new Intent(ProfilesFragment.this.activity, AddProfileActivity.class);
                ArrayList<Integer> usedPriorities = (ArrayList) ProfileAdapter.this.profileList.getUsedPriorities();
                if (usedPriorities.contains(Integer.valueOf(this.val$profile.getPriority()))) {
                    usedPriorities.remove(Integer.valueOf(this.val$profile.getPriority()));
                }
                intent.putIntegerArrayListExtra("exclusive", usedPriorities);
                intent.putExtra("profile", ProfilesFragment.this.gson.toJson(this.val$profile));
                ProfilesFragment.this.editedProfile = this.val$profile;
                ProfilesFragment.this.startActivityForResult(intent, 0);
            }
        }



        ProfileAdapter(Context context, ProfileList list) {
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.profileList = list;
        }

        public void setProfileList(ProfileList list) {
            this.profileList = list;
        }

        public int getCount() {
            return this.profileList.getProfileList().size();
        }

        public Object getItem(int position) {
            int i = 0;
            Iterator i$ = this.profileList.getProfileList().iterator();
            while (i$.hasNext()) {
                Profile profile = (Profile) i$.next();
                if (position == i) {
                    return profile;
                }
                i++;
            }
            return null;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int i, View convertView, ViewGroup parent) {
            final int position = i;
            Iterator i$;
            final Profile profile = (Profile) getItem(position);
            View view = this.inflater.inflate(R.layout.profile_item_new, null);
            ((TextView) view.findViewById(R.id.profileName)).setText(profile.getName());
            StringBuilder conditionsBuilder = new StringBuilder(48);
            if (profile.getBaseCondition().getChildren().isEmpty()) {
                conditionsBuilder.append(this.context.getString(R.string.no_conditions));
            } else {
                i$ = profile.getBaseCondition().getChildren().iterator();
                while (i$.hasNext()) {
                    conditionsBuilder.append(((Condition) i$.next()).getFormattedName(this.context));
                    conditionsBuilder.append(", ");
                }
                conditionsBuilder.deleteCharAt(conditionsBuilder.length() - 1);
                conditionsBuilder.deleteCharAt(conditionsBuilder.length() - 1);
            }
            ((TextView) view.findViewById(R.id.profileConditions)).setText(conditionsBuilder.toString());
            StringBuilder actionsBuilder = new StringBuilder(48);
            if (profile.getActionList().isEmpty()) {
                actionsBuilder.append(this.context.getString(R.string.no_actions));
            } else {
                for (Action action : profile.getActionList()) {
                    actionsBuilder.append(action.getFormattedName(this.context));
                    actionsBuilder.append(", ");
                }
                actionsBuilder.deleteCharAt(actionsBuilder.length() - 1);
                actionsBuilder.deleteCharAt(actionsBuilder.length() - 1);
            }
            ((TextView) view.findViewById(R.id.profileActions)).setText(actionsBuilder.toString());
            StringBuilder optionsBuilder = new StringBuilder();
            optionsBuilder.append(this.context.getString(R.string.priority));
            optionsBuilder.append(' ');
            optionsBuilder.append(profile.getPriority());
            optionsBuilder.append(", ");
            if (profile.isExclusive()) {
                optionsBuilder.append(this.context.getString(R.string.exclusive));
            } else {
                optionsBuilder.append(this.context.getString(R.string.not_exclusive));
            }
            ((TextView) view.findViewById(R.id.profileOptions)).setText(optionsBuilder.toString());
            SmoothCheckBox enableBox = (SmoothCheckBox) view.findViewById(R.id.profileEnable);
            enableBox.setChecked(profile.isEnabled(), true);
            enableBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {

                    profile.setEnabled(isChecked);
                    ProfilesFragment.this.save();
                }
            });
            view.findViewById(R.id.profileRow).setOnClickListener(new C01082(profile));
            //view.findViewById(R.id.icon).setOnClickListener(new C01103(profile, position));


            final ImageView overflow = (ImageView) view.findViewById(R.id.icon);
            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(getActivity(), overflow);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int oldPriority;
                            int newPriority;
                            Profile swapProfile;
                            switch (item.getItemId()) {


                                case R.id.move_down:

                                    oldPriority = profile.getPriority();
                                    if (position < ProfileAdapter.this.getCount() - 1) {
                                        newPriority = ((Profile) ProfileAdapter.this.getItem(position + 1)).getPriority();
                                        ((Profile) ProfileAdapter.this.getItem(position + 1)).setPriority(oldPriority);
                                        if (oldPriority != newPriority) {
                                            profile.setPriority(newPriority);
                                        } else if (newPriority >= 1) {
                                            profile.setPriority(newPriority - 1);
                                        }
                                        swapProfile = (Profile) ProfileAdapter.this.getItem(position + 1);
                                        ProfileAdapter.this.profileList.deleteProfile(profile);
                                        ProfileAdapter.this.profileList.deleteProfile(swapProfile);
                                        ProfileAdapter.this.profileList.addProfile(profile);
                                        ProfileAdapter.this.profileList.addProfile(swapProfile);
                                        ProfilesFragment.this.adapter.setProfileList(ProfileAdapter.this.profileList);
                                        ProfilesFragment.this.adapter.notifyDataSetChanged();
                                        ProfilesFragment.this.save();
                                    }
                                    break;


                                case R.id.move_up:
                                    oldPriority = profile.getPriority();
                                    newPriority = ((Profile) ProfileAdapter.this.getItem(position - 1)).getPriority();
                                    ((Profile) ProfileAdapter.this.getItem(position - 1)).setPriority(oldPriority);
                                    if (oldPriority != newPriority) {
                                        profile.setPriority(newPriority);
                                    } else if (newPriority <= 99) {
                                       profile.setPriority(newPriority + 1);
                                    }
                                    swapProfile = (Profile) ProfileAdapter.this.getItem(position - 1);
                                    ProfileAdapter.this.profileList.deleteProfile(profile);
                                    ProfileAdapter.this.profileList.deleteProfile(swapProfile);
                                    ProfileAdapter.this.profileList.addProfile(profile);
                                    ProfileAdapter.this.profileList.addProfile(swapProfile);
                                    ProfilesFragment.this.adapter.setProfileList(ProfileAdapter.this.profileList);
                                    ProfilesFragment.this.adapter.notifyDataSetChanged();
                                    ProfilesFragment.this.save();
                                    break;

                                case R.id.edit:
                                    Intent intent = new Intent(ProfilesFragment.this.activity, AddProfileActivity.class);
                                    ArrayList<Integer> usedPriorities = (ArrayList) ProfileAdapter.this.profileList.getUsedPriorities();
                                    if (usedPriorities.contains(Integer.valueOf(profile.getPriority()))) {
                                        usedPriorities.remove(Integer.valueOf(profile.getPriority()));
                                    }
                                    intent.putIntegerArrayListExtra("exclusive", usedPriorities);
                                    intent.putExtra("profile", ProfilesFragment.this.gson.toJson(profile));
                                    ProfilesFragment.this.editedProfile = profile;
                                    ProfilesFragment.this.startActivityForResult(intent, 0);
                                    break;


                                case R.id.delete:
                                    ProfileAdapter.this.profileList.deleteProfile(profile);
                                    ProfilesFragment.this.adapter.setProfileList(ProfileAdapter.this.profileList);
                                    ProfilesFragment.this.adapter.notifyDataSetChanged();
                                    ProfilesFragment.this.save();
                                    break;


                            }
                            return true;
                        }
                    });

                    popup.show();
                }
            });
            return view;
        }
    }

    public ProfilesFragment() {
        this.editedProfile = null;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        this.activity = getActivity();
        this.context = this.activity.getApplicationContext();
        this.settings = this.activity.getSharedPreferences("SpeedUp", 0);
        this.editor = this.settings.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Condition.class, new Serializer());
        gsonBuilder.registerTypeAdapter(Condition.class, new Deserializer());
        gsonBuilder.registerTypeAdapter(Action.class, new Action.Serializer());
        gsonBuilder.registerTypeAdapter(Action.class, new Action.Deserializer());
        gsonBuilder.disableHtmlEscaping();
        this.gson = gsonBuilder.create();
        gsonBuilder.setPrettyPrinting();
        this.gsonExport = gsonBuilder.create();
        this.profiles = new File(this.activity.getDir(PROFILES_DIR, Context.MODE_PRIVATE).getAbsolutePath() + "/" + PROFILES_FILE);
        this.serviceIntent = new Intent(this.context, ProfilesService.class);
        this.serviceIntent.putExtra("profileListPath", this.profiles.getAbsolutePath());
        if (this.profiles.exists()) {
            Log.d("SpeedUp", "found new profiles");
            try {
                if (this.profiles.exists()) {
                    this.profileList = (ProfileList) this.gson.fromJson(new FileReader(this.profiles.getAbsolutePath()), ProfileList.class);
                }
            } catch (JsonParseException e) {
                e.printStackTrace();
                Log.e("SpeedUp", "Profiles parse error! Resetting profiles.");
                this.profileList = new ProfileList();
                save();
            } catch (NullPointerException e2) {
                e2.printStackTrace();
                Log.e("SpeedUp", "Profiles parse error! Resetting profiles.");
                this.profileList = new ProfileList();
                save();
            } catch (FileNotFoundException e3) {
                e3.printStackTrace();
                Log.e("SpeedUp", "Profiles parse error! Resetting profiles.");
                this.profileList = new ProfileList();
                save();
            }
        } else if (this.activity.getDatabasePath(DBHelper.DATABASE_NAME).exists()) {
            Log.d("SpeedUp", "Found old profiles. Attempting conversion.");
            try {
                this.profileList = new ProfileConverter(this.context).convertDatabase(DBHelper.DATABASE_NAME);
            } catch (Exception e4) {
                Log.e("SpeedUp", "Profile conversion failed!");
                this.profileList = new ProfileList();
                e4.printStackTrace();
            }
            save();
        } else {
            Log.d("SpeedUp", "Didn't find any profiles. Creating new profiles.");
            this.profileList = new ProfileList();
            save();
        }
        View view = inflater.inflate(R.layout.profiles_new, container, false);
        mainFab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<FloatingActionButton> fabs = new ArrayList<FloatingActionButton>();
                fabs.add(fab1);
                fabs.add(fab2);
                fabs.add(fab3);
                isOpen = MainActivity.animateFAB(isOpen, mainFab, fabs);
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, AddProfileActivity.class);
                intent.putIntegerArrayListExtra("exclusive", (ArrayList) profileList.getUsedPriorities());
                intent.putExtra("profile", gson.toJson(new Profile()));
                editedProfile = null;
                startActivityForResult(intent, 0);

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText input = new EditText(context);
                input.setHint(context.getString(R.string.backup_edittext_hint));
                input.setMaxLines(1);
                new Builder(activity).setTitle(context.getString(R.string.enter_backup_name)).setView(input).setPositiveButton(context.getString(R.string.save), new C01053(input)).setNegativeButton(context.getString(R.string.cancel), new C01042()).show();

            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] items = getBackups();
                Builder builder = new Builder(activity);
                builder.setTitle(context.getString(R.string.restore_profiles));
                builder.setItems(items, new C01064(items));
                builder.create().show();
            }
        });
        this.adapter = new ProfileAdapter(this.context, this.profileList);
        ListView listView = (ListView) view.findViewById(R.id.profileList);
        listView.setAdapter(this.adapter);
        this.enable = (SmoothCheckBox) view.findViewById(R.id.profilesEnable);
        this.enable.setChecked(this.settings.getBoolean("profilesOn", false), true);
        this.enable.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    ProfilesFragment.this.editor.putBoolean("profilesOn", true);
                    ProfilesFragment.this.context.startService(ProfilesFragment.this.serviceIntent);
                } else {
                    ProfilesFragment.this.editor.putBoolean("profilesOn", false);
                    ProfilesFragment.this.context.stopService(ProfilesFragment.this.serviceIntent);
                }
                ProfilesFragment.this.editor.commit();
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String serializedProfile = data.getStringExtra("profile");
            if (serializedProfile != null) {
                Profile newProfile = (Profile) this.gson.fromJson(serializedProfile, Profile.class);
                if (this.editedProfile != null) {
                    this.profileList.deleteProfile(this.editedProfile);
                }
                this.profileList.addProfile(newProfile);
                this.adapter.setProfileList(this.profileList);
                this.adapter.notifyDataSetChanged();
                save();
            }
        }
        this.editedProfile = null;
    }

    private void save() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(this.profiles));
            out.write(this.gson.toJson(this.profileList));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.settings.getBoolean("profilesOn", false)) {
            this.context.stopService(this.serviceIntent);
            this.context.startService(this.serviceIntent);
        }
    }

    public Integer getTitleRes() {
        return Integer.valueOf(R.string.tab_profiles);
    }

    public Integer getMenuRes() {
        return Integer.valueOf(R.menu.profilesold);
    }

    private String[] getBackups() {
        List<String> backups = new ArrayList();
        String[] fileList = Environment.getExternalStorageDirectory().list();
        int i = 0;
        while (i < fileList.length) {
            if (fileList[i].endsWith(".SpeedUpdb") || fileList[i].endsWith(".SpeedUp.json")) {
                backups.add(fileList[i]);
            }
            i++;
        }
        return (String[]) backups.toArray(new String[0]);
    }

    private boolean backup(String filename) {
        File src = new File(Environment.getExternalStorageDirectory() + "/" + filename);
        try {
            Utils.copyFile(this.profiles, src);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void restore(String filename) {
        File src = new File(Environment.getExternalStorageDirectory() + "/" + filename);
        if (!src.exists()) {
            return;
        }
        if (filename.endsWith(".SpeedUpdb")) {
            File dest = this.activity.getDatabasePath("restore.sqlite");
            if (dest.exists()) {
                dest.delete();
            }
            try {
                if (!dest.exists()) {
                    if (!dest.getParentFile().exists()) {
                        dest.getParentFile().mkdir();
                    }
                    dest.createNewFile();
                }
                Utils.copyFile(src, dest);
                this.profileList = new ProfileConverter(this.context).convertDatabase("restore.sqlite");
                this.adapter.setProfileList(this.profileList);
                this.adapter.notifyDataSetChanged();
                save();
            } catch (IOException e) {
            } catch (Exception e2) {
            }
        } else if (filename.endsWith(".SpeedUp.json")) {
            try {
                this.profileList = (ProfileList) this.gson.fromJson(new FileReader(src), ProfileList.class);
                this.adapter.setProfileList(this.profileList);
                this.adapter.notifyDataSetChanged();
                save();
            } catch (JsonSyntaxException e3) {
            } catch (JsonIOException e4) {
            } catch (FileNotFoundException e5) {
            }
        } else {
        }
    }

    private void email() {
        String version = Utils.readFile("/proc/version");
        if (version == null) {
            version = "";
        }
        String subject = this.context.getString(R.string.SpeedUp_profiles);
        Intent sendIntent = new Intent("android.intent.action.SEND");
        sendIntent.putExtra("android.intent.extra.SUBJECT", subject);
        sendIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(this.profiles));
        sendIntent.putExtra("android.intent.extra.SUBJECT", subject);
        sendIntent.putExtra("android.intent.extra.TEXT", this.context.getString(R.string.kernel) + ": " + version.trim());
        sendIntent.setType("application/json");
        try {
            Runtime.getRuntime().exec("chmod 644 " + this.profiles.getAbsolutePath());
            startActivity(Intent.createChooser(sendIntent, this.context.getString(R.string.send)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
