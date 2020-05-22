/*
 * RRRRRR                         jjj
 * RR   RR   eee    eee               pp pp
 * RRRRRR  ee   e ee   e _____    jjj ppp  pp
 * RR  RR  eeeee  eeeee           jjj pppppp
 * RR   RR  eeeee  eeeee          jjj pp
 *                              jjjj  pp
 *
 * Copyright (c) 2020. Ree-jp.  All Rights Reserved.
 */

package net.ree_jp.floatingchat.event

import cn.nukkit.Server
import cn.nukkit.entity.Entity
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerChatEvent
import cn.nukkit.event.player.PlayerMoveEvent
import net.ree_jp.floatingchat.FloatingChatPlugin

class EventListener : Listener {

    private val particle = mutableMapOf<String, Entity>()

    @EventHandler
    fun onChat(ev: PlayerChatEvent) {
        val p = ev.player
        val xuid = p.loginChainData.xuid

        if (p.hasPermission("floatingChat.show")) {
            val entity = particle[xuid] ?: Entity.createEntity("FloatingChat", p)
            entity.setScale(0f)
            entity.nameTag = ev.message
            entity.setNameTagVisible()
            entity.spawnToAll()
            particle[xuid] = entity
            Server.getInstance().scheduler.scheduleDelayedTask(
                FloatingChatPlugin.instance,
                {
                    particle.remove(xuid)
                    entity.close()
                }, 100
            )
        }
    }

    @EventHandler
    fun onMove(ev: PlayerMoveEvent) {
        val p = ev.player
        val xuid = p.loginChainData.xuid
        val entity = particle[xuid] ?: return

        entity.x = p.x
        entity.y = p.y
        entity.z = p.z
    }
}