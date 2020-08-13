package com.truckexpress.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.truckexpress.Models.UserInfo;

@Database(
        entities = {UserInfo.class},
        version = 1,
        exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    public abstract DBAccess dbAccess();
}