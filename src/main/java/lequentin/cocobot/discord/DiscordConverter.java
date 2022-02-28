package lequentin.cocobot.discord;

import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.User;

public class DiscordConverter {

    public Message toDomain(discord4j.core.object.entity.Message discordMessage) {
        discord4j.core.object.entity.User discordUser = discordMessage.getAuthor()
                .orElseThrow(() -> new RuntimeException("Message has no user"));
        User author = new User(discordUser.getUsername());
        return new Message(author, discordMessage.getContent());
    }

}
