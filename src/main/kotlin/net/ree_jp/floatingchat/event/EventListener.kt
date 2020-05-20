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
import cn.nukkit.entity.data.EntityMetadata
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerChatEvent
import cn.nukkit.event.player.PlayerMoveEvent
import cn.nukkit.level.Position
import cn.nukkit.network.protocol.AddPlayerPacket
import cn.nukkit.network.protocol.MoveEntityAbsolutePacket
import cn.nukkit.network.protocol.RemoveEntityPacket
import net.ree_jp.floatingchat.FloatingChatPlugin
import java.util.*

class EventListener : Listener {

    private val show = mutableMapOf<String, Long>()

    private val chat = mutableMapOf<String, String>()

    @EventHandler
    fun onChat(ev: PlayerChatEvent) {
        val p = ev.player
        val xuid = p.loginChainData.xuid

        if (p.hasPermission("floatingChat.show")) {
            deleteIfExists(xuid)
            val particle = createFloatingText(p.add(0.0, 1.5), ev.message)
            show[xuid] = particle
            chat[xuid] = ev.message
            Server.getInstance().scheduler.scheduleDelayedTask(
                FloatingChatPlugin.instance,
                {
                    show.remove(xuid)
                    deleteFloatingText(particle)
                }, 100
            )
        }
    }

    @EventHandler
    fun onMove(ev: PlayerMoveEvent) {
        val p = ev.player
        val xuid = p.loginChainData.xuid
        val eid = show[xuid] ?: return
        val text = chat[xuid] ?: return

        reCreateFloatingText(eid, text, p.add(0.0, 1.5))
    }

    private fun deleteIfExists(xuid: String) {
        val eid = show[xuid] ?: return
        deleteFloatingText(eid)
    }

    private fun createFloatingText(pos: Position, title: String): Long {
        return createFloatingText(pos, title, Random().nextLong())
    }

    private fun createFloatingText(pos: Position, title: String, eid: Long): Long {
        val pk = AddPlayerPacket()
        pk.uuid = UUID.randomUUID()
        pk.username = title
        pk.entityUniqueId = eid
        pk.x = pos.x.toFloat()
        pk.y = pos.y.toFloat()
        pk.z = pos.z.toFloat()
        pk.metadata = EntityMetadata().putFloat(Entity.DATA_SCALE, 0.toFloat())
        for (p in pos.level.players.values) {
            p.dataPacket(pk)
        }
        return eid
    }

    /**
     * 動かないので分かるかたいたらお願いします
     */
    private fun moveFloatingText(eid: Long, pos: Position) {
        val pk = MoveEntityAbsolutePacket()
        pk.eid = eid
        pk.putVector3f(pos.asVector3f())
        for (p in pos.level.players.values) {
            p.dataPacket(pk)
        }
    }

    /**
     * 上のが動かないから臨時で
     */
    private fun reCreateFloatingText(eid: Long, title: String, pos: Position) {
        deleteFloatingText(eid)
        createFloatingText(pos, title, eid)
    }

    private fun deleteFloatingText(eid: Long) {
        val pk = RemoveEntityPacket()
        pk.eid = eid
        for (p in Server.getInstance().onlinePlayers.values) {
            p.dataPacket(pk)
        }
    }
}