import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

public final class Main {
    public static void main(final String[] args) {
        final String token = args[0];
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();

        final MessageClient messageClient = new MessageClient();
        final CocoBot coco = new CocoBot(messageClient);

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            coco.handleMessage(message);
        });

        gateway.onDisconnect().block();
    }
}
