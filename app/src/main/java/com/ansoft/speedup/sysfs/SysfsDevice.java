package com.ansoft.speedup.sysfs;

import com.ansoft.speedup.util.Shell;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SysfsDevice {
    Map<String, Attr> mAttrMap;
    Map<String, SysfsDevice> mChildren;
    String mPath;
    int mRecurse;

    public static class Attr {
        transient String mCurrentVal;
        transient File mFile;
        String mName;
        boolean mReadOnly;
        String mVal;

        Attr(File file) {
            this.mName = file.getName();
        }

        public boolean isDirectory() {
            return this.mFile.isDirectory();
        }

        public String read(Shell shell) {
            return null;
        }

        public boolean write(Shell shell, String value) {
            try {
                shell.writeLine("echo " + value + " > " + this.mFile.getAbsolutePath());
                shell.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    class Deserializer {
        Deserializer() {
        }
    }

    class Serializer {
        Serializer() {
        }
    }

    public SysfsDevice(String path, int recurse) {
        this.mRecurse = recurse;
        File dir = new File(path);
        this.mAttrMap = new HashMap();
        for (File attr : dir.listFiles()) {
            if (attr.isFile()) {
                Attr cur = new Attr(attr);
                this.mAttrMap.put(cur.mName, cur);
            } else if (attr.isDirectory() && this.mRecurse > 0) {
                this.mChildren.put(attr.getName(), new SysfsDevice(attr.getAbsolutePath(), this.mRecurse - 1));
            }
        }
    }

    public Attr getAttr(String name) {
        return (Attr) this.mAttrMap.get(name);
    }

    public SysfsDevice getChild(String name) {
        return (SysfsDevice) this.mChildren.get(name);
    }

    public Collection<Attr> getAttrs() {
        return this.mAttrMap.values();
    }

    public Collection<SysfsDevice> getChildren() {
        return this.mChildren.values();
    }
}
