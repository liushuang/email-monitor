package com.renren.mail.monitor.disc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renren.mail.monitor.dao.EmailTaskDAO;
import com.renren.mail.monitor.disc.ChangeDisc;

@Service
public class ChangeDiscImpl implements ChangeDisc {

    @Autowired
    private EmailTaskDAO emailDao;

    @Override
    public void changeDiscId(int scourceDiscId, int targetDiscId) {
        emailDao.updateDiscId(scourceDiscId, targetDiscId);
    }

    @Override
    public int changeNextDiscId(int discId) {
        // 如果存在下一个disc,修改disc号
        if (emailDao.getDiscIdCount(discId + 1) > 0) {
            changeDiscId(discId, discId + 1);
            return discId + 1;
        } else {
            return 0;
        }
    }

}
