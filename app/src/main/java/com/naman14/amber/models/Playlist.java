/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.amber.models;

public class Playlist {

    public final long id;
    public String onlineId;
    public final String name;
    public final int songCount;
    public boolean isOnline = false;
    public String listPic;
    public int shareCount;

    public Playlist() {
        this.id = -1;
        this.name = "";
        this.songCount = -1;
        this.isOnline = false;
    }

    public Playlist(long _id, String _name, int _songCount) {
        this.id = _id;
        this.name = _name;
        this.songCount = _songCount;
        this.isOnline = false;
    }

    public Playlist(String _id, String _name, int _songCount, boolean _isOnline, int _shareCount, String _listPic) {
        this.id = 0;
        this.onlineId = _id;
        this.name = _name;
        this.songCount = _songCount;
        this.isOnline = _isOnline;
        this.shareCount = _shareCount;
        this.listPic = _listPic;
    }
}
