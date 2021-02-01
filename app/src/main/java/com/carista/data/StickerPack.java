package com.carista.data;

import java.io.Serializable;
import java.util.List;

public class StickerPack implements Serializable {

    public String title;
    public String icon;

    public List<StickerItem> items;

    public StickerPack(String title, String icon, List<StickerItem> items) {
        this.icon = icon;
        this.title = title;
        this.items = items;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<StickerItem> getItems() {
        return items;
    }

    public void setItems(List<StickerItem> items) {
        this.items = items;
    }
}
