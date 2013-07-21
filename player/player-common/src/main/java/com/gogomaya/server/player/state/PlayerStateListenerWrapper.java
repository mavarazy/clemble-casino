package com.gogomaya.server.player.state;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.gogomaya.server.player.PlayerState;

public class PlayerStateListenerWrapper implements MessageListener {

    final private RedisSerializer<String> stringRedisSerializer;
    final private PlayerStateListener playerStateListener;

    public PlayerStateListenerWrapper(RedisSerializer<String> stringRedisSerializer, PlayerStateListener playerStateListener) {
        this.stringRedisSerializer = checkNotNull(stringRedisSerializer);
        this.playerStateListener = checkNotNull(playerStateListener);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // Step 1. Reading channel and message
        String deserializedChannel = stringRedisSerializer.deserialize(message.getChannel());
        String deserializedMessage = stringRedisSerializer.deserialize(message.getBody());
        // Step 2. Notifying associated PlayerStateListener
        playerStateListener.onUpdate(Long.valueOf(deserializedChannel), PlayerState.valueOf(deserializedMessage));

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((playerStateListener == null) ? 0 : playerStateListener.hashCode());
        result = prime * result + ((stringRedisSerializer == null) ? 0 : stringRedisSerializer.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerStateListenerWrapper other = (PlayerStateListenerWrapper) obj;
        if (playerStateListener == null) {
            if (other.playerStateListener != null)
                return false;
        } else if (!playerStateListener.equals(other.playerStateListener))
            return false;
        if (stringRedisSerializer == null) {
            if (other.stringRedisSerializer != null)
                return false;
        } else if (!stringRedisSerializer.equals(other.stringRedisSerializer))
            return false;
        return true;
    }

}
