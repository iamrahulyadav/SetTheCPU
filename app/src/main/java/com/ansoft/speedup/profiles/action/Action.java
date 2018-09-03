package com.ansoft.speedup.profiles.action;

import android.content.Context;
import android.os.Bundle;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ansoft.speedup.profiles.Profile;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

public class Action {
    protected String type;

    public static class TypeMap {
        private static LinkedHashMap<String, Class<? extends Action>> map;

        static {
            map = new LinkedHashMap();
            map.put("Action", Action.class);
            map.put("CpuFrequency", CpuFrequencyAction.class);
            map.put("CpuGovernor", CpuGovernorAction.class);
            map.put("IoScheduler", IoSchedulerAction.class);
            map.put("Notification", NotificationAction.class);
        }

        public static Class<? extends Action> get(String key) {
            return (Class) map.get(key);
        }

        public static Set<String> keySet() {
            return map.keySet();
        }

        public static Collection<Class<? extends Action>> values() {
            return map.values();
        }
    }

    public static class Deserializer implements JsonDeserializer<Action> {
        public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return (Action) context.deserialize(json, TypeMap.get(json.getAsJsonObject().get("type").getAsString()));
        }
    }

    public static class Serializer implements JsonSerializer<Action> {
        public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src, src.getType());
        }
    }

    public Action() {
        this.type = "Action";
    }

    public boolean set(Bundle args) {
        return false;
    }

    public String getFormattedName(Context context) {
        return null;
    }

    public String getName(Context context) {
        return null;
    }

    public Class<? extends Action> getType() {
        return TypeMap.get(this.type);
    }

    public String getTypeName() {
        return this.type;
    }

    public boolean perform(ActionPerformer performer, Profile profile) {
        return false;
    }

    public boolean perform(ActionPerformer performer) {
        return false;
    }

    public Bundle get() {
        return null;
    }
}
