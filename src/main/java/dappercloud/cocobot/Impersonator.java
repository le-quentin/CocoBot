package dappercloud.cocobot;

import discord4j.core.object.entity.User;

public interface Impersonator {
    void buildModel();
    String impersonate(User user);
}
