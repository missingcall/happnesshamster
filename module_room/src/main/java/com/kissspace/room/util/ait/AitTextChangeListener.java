package com.kissspace.room.util.ait;

/**
 * Created by hzchenkang on 2017/7/10.
 */

public interface AitTextChangeListener {

    void onAitPageOpen();

    void onAitPageClose();

    void onTextAdd(String content, int start, int length);

    void onTextDelete(int start, int length);
}
