package moe.plushie.armourers_workshop.core.skin.serializer;

import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;

public class SkinFile implements Comparable<SkinFile> {

    protected final String name;
    protected final String path;
    protected final DataDomain domain;
    protected final SkinFileHeader header;
    protected final boolean isDirectory;
    protected final boolean isPrivateDirectory;

    public SkinFile(DataDomain domain, String name, String path, SkinFileHeader header, boolean isDirectory, boolean isPrivateDirectory) {
        this.domain = domain;
        this.name = name;
        this.path = path;
        this.header = null;
        this.isDirectory = isDirectory;
        this.isPrivateDirectory = isPrivateDirectory;
    }

    public boolean isSameFile(SkinFile other) {
        return name.equals(other.name) && path.equals(other.path) && domain.equals(other.domain);
    }


    public String getName() {
        return name;
    }

    public String getNamespace() {
        return domain.namespace();
    }

    public String getPath() {
        return path;
    }

    public int getLastModified() {
        if (header != null) {
            return header.getLastModified();
        }
        return 0;
    }

    public int getSkinVersion() {
        if (header != null) {
            return header.getVersion();
        }
        return 0;
    }

    public String getSkinIdentifier() {
        return getNamespace() + ":" + getPath();
    }

    public SkinType getSkinType() {
        if (header != null) {
            return header.getType();
        }
        return null;
    }

    public SkinFileHeader getSkinHeader() {
        return header;
    }

    public SkinProperties getSkinProperties() {
        if (header != null) {
            return header.getProperties();
        }
        return null;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public boolean isPrivateDirectory() {
        return isPrivateDirectory;
    }

    @Override
    public int compareTo(SkinFile o) {
        if (isDirectory & !o.isDirectory) {
            return path.compareToIgnoreCase(o.path) - 1000000;
        } else if (!isDirectory & o.isDirectory) {
            return path.compareToIgnoreCase(o.path) + 1000000;
        }
        return path.compareToIgnoreCase(o.path);
    }

    @Override
    public String toString() {
        return domain.normalize(path);
    }

    public boolean isChildDirectory(String rootPath) {
        // /xxxx/
        int length = rootPath.length();
        return length < path.length() && path.startsWith(rootPath) && path.indexOf('/', length) < 0;
    }
}
