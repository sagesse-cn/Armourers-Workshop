package moe.plushie.armourers_workshop.library.data.impl;

import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;

import java.util.HashMap;

public class ServerSkin {

    protected String id;
    protected String userId;
    protected String name;
    protected String description;

    protected int downloads = 0;
    protected float rating = 0;
    protected int ratingCount = 0;

    public boolean showsDownloads = true;
    public boolean showsRating = false;
    public boolean showsGlobalId = true;

    protected final SkinDescriptor descriptor;

    public ServerSkin(String id, String name, SkinDescriptor descriptor) {
        this.id = id;
        this.name = name;
        this.descriptor = descriptor;
    }

    public ServerSkin(IODataObject object) {
        this.id = object.get("id").stringValue();
        this.userId = object.get("user_id").stringValue();
        this.name = object.get("name").stringValue();
        this.description = object.get("description").stringValue();
        this.descriptor = new SkinDescriptor(DataDomain.GLOBAL_SERVER_PREVIEW.normalize(id));
        this.showsDownloads = object.has("downloads");
        if (this.showsDownloads) {
            this.downloads = object.get("downloads").intValue();
        }
        if (object.has("rating")) {
            this.rating = object.get("rating").floatValue();
            this.showsRating = true;
        }
        if (object.has("rating_count")) {
            this.ratingCount = object.get("rating_count").intValue();
            this.showsRating = true;
        }
    }

    public void update(String name, String desc, IResultHandler<ServerSkin> handler) {
        var parameters = new HashMap<String, Object>();
        parameters.put("name", name);
        parameters.put("description", desc);
        parameters.put("skinId", id);
        parameters.put("skinOwner", userId);
        getLibrary().request("/skin/edit", parameters, null, (response, exception) -> {
            if (exception == null) {
                this.name = name;
                this.description = desc;
                handler.accept(this);
            } else {
                handler.throwing(exception);
            }
        });
    }

    public void getRate(IResultHandler<Integer> handler) {
        var parameters = new HashMap<String, Object>();
        parameters.put("skinId", id);
        getLibrary().request("/skin/rating", parameters, o -> o.get("rating").intValue(), handler);
    }


    public void updateRate(int rate, IResultHandler<Integer> handler) {
        var parameters = new HashMap<String, Object>();
        parameters.put("skinId", id);
        parameters.put("rating", rate);
        getLibrary().request("/skin/rate", parameters, o -> o.get("rating").intValue(), (rating, exception) -> {
            if (exception == null) {
                this.rating = rating;
            }
            handler.apply(rate, exception);
        });
    }


    public void remove(IResultHandler<Void> handler) {
        var parameters = new HashMap<String, Object>();
        parameters.put("skinId", id);
        parameters.put("skinOwner", userId);
        getLibrary().request("/skin/delete", parameters, null, handler);
    }

    public void report(String message, ReportType reportType, IResultHandler<Void> handler) {
        var parameters = new HashMap<String, Object>();
        parameters.put("reportSkinId", id);
        parameters.put("reportType", reportType.toString());
        parameters.put("reportMessage", message);
        getLibrary().request("/skin/report", parameters, null, handler);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public ServerUser getUser() {
        return getLibrary().getUserById(userId);
    }

    public SkinDescriptor getDescriptor() {
        return descriptor;
    }


    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public int getDownloads() {
        return downloads;
    }

    public float getRating() {
        return rating;
    }

    private GlobalSkinLibrary getLibrary() {
        return GlobalSkinLibrary.getInstance();
    }
}
