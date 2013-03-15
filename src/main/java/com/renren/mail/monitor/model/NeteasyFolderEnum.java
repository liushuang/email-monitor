/**
 * 
 */
package com.renren.mail.monitor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 网易邮箱的文件夹名称和代表数字的对应
 * 
 * @author liushuang
 * @creation_date 2013-3-14
 */
public enum NeteasyFolderEnum {
    /** 默认的，数字为0 */
    DEFAULT("DEFAULT", 0),
    /** 收件箱，数字为1 */
    INBOX("INBOX", 1),
    /** 订阅邮件，数字为2 */
    DINGYUE("订阅邮件", 2),
    /** 垃圾邮件，数字为3 */
    GARBAGE("垃圾邮件", 3);

    private int folderNumber;

    private String folderName;

    private NeteasyFolderEnum(String folderName, int folderNumber) {
        this.folderName = folderName;
        this.folderNumber = folderNumber;
    }

    /**
     * 根据邮件的文件夹的名字获取对应数字
     * 
     * @param folderName 文件夹的名字
     * @return 对应的数字
     */
    public static int getFolderNumberByName(String folderName) {
        NeteasyFolderEnum[] neteasyFolderEnumValues = NeteasyFolderEnum.values();
        for (NeteasyFolderEnum n : neteasyFolderEnumValues) {
            if (folderName.equals(n.folderName)) {
                return n.folderNumber;
            }
        }
        return NeteasyFolderEnum.DEFAULT.folderNumber;
    }

    /**
     * 获取文件夹的列表，目前返回收件箱、订阅邮件和垃圾邮件
     * 
     * @return 文件夹的列表的list
     */
    public static List<String> getFolderList() {
        List<String> folderList = new ArrayList<String>();
        folderList.add(INBOX.folderName);
        folderList.add(DINGYUE.folderName);
        folderList.add(GARBAGE.folderName);
        return folderList;
    }

    public static void main(String args[]) {
        System.out.println(NeteasyFolderEnum.DINGYUE.folderNumber);
        System.out.println(NeteasyFolderEnum.getFolderNumberByName("INBOX"));
    }
}
