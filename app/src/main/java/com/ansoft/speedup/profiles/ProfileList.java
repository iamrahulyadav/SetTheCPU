package com.ansoft.speedup.profiles;

import com.ansoft.speedup.profiles.condition.Condition;
import com.ansoft.speedup.profiles.condition.Condition.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ProfileList {
    private transient TreeSet<Profile> activeProfiles;
    private Set<Profile> profiles;
    public long version;

    public ProfileList() {
        this.version = 1;
        this.profiles = null;
        if (this.profiles == null) {
            this.profiles = new TreeSet();
        } else {
            this.profiles = new TreeSet(this.profiles);
        }
        this.activeProfiles = new TreeSet();
    }

    public TreeSet<Profile> getProfileList() {
        return new TreeSet(this.profiles);
    }

    public void addProfile(Profile profile) {
        this.profiles.add(profile);
    }

    public void deleteProfile(Profile profile) {
        this.profiles.remove(profile);
    }

    public Multimap<String, Condition> getIntents() {
        Multimap<String, Condition> map = new Multimap();
        for (Profile profile : this.profiles) {
            if (profile.isEnabled()) {
                profile.registerIntents(map);
            }
        }
        return map;
    }

    public Multimap<Class<? extends Scanner>, Condition> getScanners() {
        Multimap<Class<? extends Scanner>, Condition> map = new Multimap();
        for (Profile profile : this.profiles) {
            if (profile.isEnabled()) {
                profile.registerScanners(map);
            }
        }
        return map;
    }

    public List<Integer> getUsedPriorities() {
        ArrayList<Integer> priorities = new ArrayList();
        for (Profile profile : this.profiles) {
            priorities.add(Integer.valueOf(profile.getPriority()));
        }
        return priorities;
    }

    public TreeSet<Profile> getActiveProfiles() {
        this.activeProfiles.clear();
        if (!(this.profiles instanceof TreeSet)) {
            this.profiles = new TreeSet(this.profiles);
        }
        for (Profile profile : this.profiles) {
            if (profile.isEnabled() && profile.check()) {
                this.activeProfiles.add(profile);
                if (profile.isExclusive()) {
                    break;
                }
            }
        }
        return this.activeProfiles;
    }
}
