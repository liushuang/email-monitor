package com.renren.mail.monitor.disc;

public interface ChangeDisc {

    public void changeDiscId(int sourceDiscId, int targetDiscId);

    public int changeNextDiscId(int discId);
}
