package com.carista.photoeditor;

import com.carista.data.StickerPack;

import java.util.List;

public interface OnStickersPackLoad {

    void onStickersPacksFetching(List<StickerPack> packs);
}
