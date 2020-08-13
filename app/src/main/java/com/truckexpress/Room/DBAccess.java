package com.truckexpress.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.truckexpress.Models.UserInfo;
@Dao
public interface DBAccess {

    @Insert
    void insertUser(UserInfo userinfoItem);

   /* @Insert
    @Query("SELECT * FROM UserinfoItem WHERE uMID = :uMID")
    UserinfoItem user(int uMID);*/

    @Query("Select * From UserInfo")
    UserInfo getUserDetail();

    @Delete
    void deleteUser(UserInfo user);


}
