package fr.secraid.hermes.commands;

import net.dv8tion.jda.api.entities.channel.ChannelType;

public enum GlobalChannelType {
    TEXT(ChannelType.TEXT),
    PRIVATE(ChannelType.PRIVATE),
    VOICE(ChannelType.VOICE),
    GROUP(ChannelType.GROUP),
    CATEGORY(ChannelType.CATEGORY),
    NEWS(ChannelType.NEWS),
    STAGE(ChannelType.STAGE),
    GUILD_NEWS_THREAD(ChannelType.GUILD_NEWS_THREAD),
    GUILD_PUBLIC_THREAD(ChannelType.GUILD_PUBLIC_THREAD),
    GUILD_PRIVATE_THREAD(ChannelType.GUILD_PRIVATE_THREAD),
    FORUM(ChannelType.FORUM),
    GLOBAL_GUILD_TEXT(ChannelType.TEXT, ChannelType.NEWS, ChannelType.VOICE, ChannelType.FORUM, ChannelType.STAGE),
    ALL();

    private final ChannelType[] channelTypes;

    GlobalChannelType(ChannelType... channelTypes) {
        this.channelTypes = channelTypes;
    }

    public ChannelType[] getChannelTypes() {
        return channelTypes;
    }
}
