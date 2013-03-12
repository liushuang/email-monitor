package com.renren.mail.monitor.model;

import java.util.Date;

/**
 * 每条邮件记录的实体类
 * 
 * @author liushuang
 * @creationDate 2013-03-07
 */
public class EmailMonitorRecord {

    private int id;

    private String senderDomain;

    private String senderEmail;

    private String senderName;

    private String subject;

    private int folder;

    private String receverDomain;

    private String receverEmail;

    private int receverGruop;

    private Date sendDate;

    private Date checkDate;

    private String senderIp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderDomain() {
        return senderDomain;
    }

    public void setSenderDomain(String senderDomain) {
        this.senderDomain = senderDomain;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getFolder() {
        return folder;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public String getReceverDomain() {
        return receverDomain;
    }

    public void setReceverDomain(String receverDomain) {
        this.receverDomain = receverDomain;
    }

    public String getReceverEmail() {
        return receverEmail;
    }

    public void setReceverEmail(String receverEmail) {
        this.receverEmail = receverEmail;
    }

    public int getReceverGruop() {
        return receverGruop;
    }

    public void setReceverGruop(int receverGruop) {
        this.receverGruop = receverGruop;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public String getSenderIp() {
        return senderIp;
    }

    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }
}
