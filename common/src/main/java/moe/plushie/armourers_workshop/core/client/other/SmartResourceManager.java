package moe.plushie.armourers_workshop.core.client.other;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractPackResources;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.server.packs.PackType;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SmartResourceManager {

    private static final SmartResourceManager INSTANCE = new SmartResourceManager();

    protected final String id;
    protected final Set<String> namespaces = Collections.immutableSet(builder -> builder.add(ModConstants.MOD_ID));
    protected final Map<IResourceLocation, ByteBuf> resources = new ConcurrentHashMap<>();

    protected SmartResourceManager() {
        this.id = String.format("dynamic/%s", ModConstants.MOD_ID);
    }

    public static SmartResourceManager getInstance() {
        return INSTANCE;
    }

    public void register(IResourceLocation location, ByteBuf buffer) {
        ModLog.debug("Registering Resource '{}'", location);
        resources.put(location, buffer);
    }

    public void unregister(IResourceLocation location) {
        ModLog.debug("Unregistering Resource '{}'", location);
        resources.remove(location);
    }

    public Supplier<InputStream> getResource(PackType packType, IResourceLocation location) {
        var buf = resources.get(location);
        if (buf != null) {
            return () -> new ByteBufInputStream(buf.slice());
        }
        return null;
    }

    public AbstractPackResources getResources(PackType packType) {
        return new AbstractPackResources(this, packType);
    }

    public Set<String> getNamespaces(PackType packType) {
        return namespaces;
    }

    public String getId() {
        return id;
    }
}
