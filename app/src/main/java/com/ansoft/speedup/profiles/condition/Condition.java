package com.ansoft.speedup.profiles.condition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ansoft.speedup.profiles.Scanner;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Condition {
    protected transient int category;
    protected transient Set<String> intents;
    protected transient Condition parent;
    protected transient Set<Class<? extends Scanner>> scanners;
    protected String type;

    public static class Multimap<Key, Entry> {
        private HashMap<Key, List<Entry>> map;

        public Multimap() {
            this.map = new HashMap();
        }

        public boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        public List<Entry> get(Object key) {
            return (List) this.map.get(key);
        }

        public Set<Key> keySet() {
            return this.map.keySet();
        }

        public void clear() {
            this.map.clear();
        }

        public void add(Key key, Entry entry) {
            if (this.map.containsKey(key)) {
                ((List) this.map.get(key)).add(entry);
                return;
            }
            ArrayList<Entry> entryList = new ArrayList();
            entryList.add(entry);
            this.map.put(key, entryList);
        }
    }

    public static class TypeMap {
        private static Map<String, Class<? extends Condition>> map;

        static {
            map = new LinkedHashMap();
            map.put("App", AppState.class);
            map.put("BatteryLevel", BatteryLevelState.class);
            map.put("BatteryTemp", BatteryTempState.class);
            map.put("Charge", ChargeState.class);
            map.put("CpuTemp", CpuTempState.class);
            map.put("Screen", ScreenState.class);
            map.put("ScreenUnlocked", UserPresentState.class);
            map.put("Call", CallState.class);
            map.put("TimeRange", TimeRangeState.class);
            map.put("DayOfWeek", DayOfWeekState.class);
            map.put("All", LogicAll.class);
            map.put("Any", LogicAny.class);
            map.put("None", LogicNone.class);


        }

        public static Class<? extends Condition> get(String key) {
            return (Class) map.get(key);
        }

        public static Set<String> keySet() {
            return map.keySet();
        }

        public static Collection<Class<? extends Condition>> values() {
            return map.values();
        }

        public static Map<String, Class<? extends Condition>> getMaps(int pos){
            return map;
        }
    }

    public static class Deserializer implements JsonDeserializer<Condition> {
        public Condition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return (Condition) context.deserialize(json, TypeMap.get(json.getAsJsonObject().get("type").getAsString()));
        }
    }

    public static class Serializer implements JsonSerializer<Condition> {
        public JsonElement serialize(Condition src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src, src.getType());
        }
    }

    public Condition() {
        this.type = "Condition";
        this.parent = null;
        this.intents = null;
        this.scanners = null;
        this.intents = new HashSet();
        this.scanners = new HashSet();
    }

    public boolean check() {
        return false;
    }

    public boolean updateState(Bundle extras) {
        return false;
    }

    public boolean updateState(Intent intent) {
        return false;
    }

    public boolean updateState() {
        return false;
    }

    public boolean set(Bundle args) {
        return false;
    }

    public Bundle get() {
        return null;
    }

    public void setParent(Condition parent) {
        this.parent = parent;
    }

    public Condition getParent() {
        return this.parent;
    }

    public ArrayList<Condition> getChildren() {
        return null;
    }

    public void addChild(Condition condition) {
    }

    public boolean deleteChild(Condition condition) {
        return false;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public boolean hasIntents() {
        return this.intents != null;
    }

    public boolean hasScanners() {
        return this.scanners != null;
    }

    public String getFormattedName(Context context) {
        return null;
    }

    public String getName(Context context) {
        return null;
    }

    public Class<?> getConfigFragment() {
        return null;
    }

    public int getCategory() {
        return this.category;
    }

    public Class<? extends Condition> getType() {
        return TypeMap.get(this.type);
    }

    public String getTypeName() {
        return this.type;
    }

    public boolean deleteSelf() {
        if (hasParent()) {
            return this.parent.deleteChild(this);
        }
        return false;
    }

    public void registerIntents(Multimap<String, Condition> map) {
        Iterator i$;
        for (String string : this.intents) {
            map.add(string, this);
        }
        if (getChildren() != null) {
            i$ = getChildren().iterator();
            while (i$.hasNext()) {
                ((Condition) i$.next()).registerIntents(map);
            }
        }
    }

    public void registerScanners(Multimap<Class<? extends Scanner>, Condition> map) {
        Iterator i$;
        for (Class<? extends Scanner> scanner : this.scanners) {
            map.add(scanner, this);
        }
        if (getChildren() != null) {
            i$ = getChildren().iterator();
            while (i$.hasNext()) {
                ((Condition) i$.next()).registerScanners(map);
            }
        }
    }

    public boolean available() {
        return true;
    }
}
