package moe.plushie.armourers_workshop.api.skin.serializer;

import java.io.InputStream;

public interface ISkinFileProvider {

    InputStream loadSkin(String skinId) throws Exception;
}
