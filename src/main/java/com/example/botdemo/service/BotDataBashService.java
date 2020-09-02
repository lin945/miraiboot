package com.example.botdemo.service;

import com.example.botdemo.component.BotComponent;
import com.example.botdemo.dao.DataBaseDao;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lin945
 * @date 2020/8/30 14:10
 */
@Service
public class BotDataBashService {
    @Autowired
    private DataBaseDao dataBaseDao;

    public String getNick() {
       return BotComponent.getBot().getNick();
    }

    public ContactList<Friend> etFriends(){
        return BotComponent.getBot().getFriends();
    }

    public boolean isOnline() {
        return BotComponent.getBot().isOnline();
    }

    public long getQQ(String token) {
        return dataBaseDao.getUser(token);
    }
    public void sendMessage(String token,String mTest){

    BotComponent.getBot().getFriend(getQQ(token)).sendMessage(mTest);
    }

}
