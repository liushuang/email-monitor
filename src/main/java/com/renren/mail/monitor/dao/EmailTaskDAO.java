package com.renren.mail.monitor.dao;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

@DAO(catalog = "rrop_letter")
public interface EmailTaskDAO {

    @SQL("select count(1) from email_task")
    public int getTaskCount();

    /**
     * 更新一个指定的discid为另外一个discid
     * @param sourceDiscId 原有的discid
     * @param targetDiscId 改变之后的discid
     */
    @SQL("update email_task set discid = :targetDiscId where discid = :sourceDiscId")
    public void updateDiscId(@SQLParam("sourceDiscId") int sourceDiscId,
            @SQLParam("targetDiscId") int targetDiscId);

    /**
     * 根据discid获取记录的数量，0代表没有，1代表存在该discid
     * @param discId discid
     * @return 0代表有该discid，1代表没有该discid
     */
    @SQL("select count(1) from email_disc where template_id = :discid")
    public int getDiscIdCount(@SQLParam("discid") int discId);
}
