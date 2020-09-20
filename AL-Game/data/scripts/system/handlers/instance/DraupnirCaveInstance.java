package instance;

import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Created by xnemonx on 04/12/17.
 */
@InstanceID(320080000)
public class DraupnirCaveInstance extends GeneralInstanceHandler {
    protected boolean isInstanceDestroyed = false;
    private int bakarmaCharger;
    private int adjutantsKilled;
    private Future<?> abyssGateTask;

    @Override
    public void onEnterInstance(final Player player) {
        super.onInstanceCreate(instance);
        //You must kill Afrane, Saraswati, Lakshmi, and Nimbarka to make Commander Bakarma appear.
        sendMsgByRace(1400757, Race.PC_ALL, 10000);
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        switch (Rnd.get(1, 4)) {
            case 1:
                spawn(213587, 567.438f, 700.875f, 538.701f, (byte) 7); //Hungry Ooze.
                break;
            case 2:
                spawn(213588, 166.8f, 536.285f, 505.802f, (byte) 9); //Lucky Golden Saam.
                break;
            case 3:
                spawn(213771, 497.006f, 434.713f, 616.584f, (byte) 71); //Protector Rakkan.
                break;
            case 4:
                spawn(213773, 380.694f, 611.956f, 598.523f, (byte) 98); //Dragonpriest Tairgus.
                break;
        }
    }

    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
        switch (npc.getObjectTemplate().getTemplateId()) {
            case 213776: //Instructor Afrane.
            case 213778: //Beautiful Lakshmi.
            case 213779: //Commander Nimbarka.
            case 213802: //Kind Saraswati.
                adjutantsKilled++;
                if (adjutantsKilled == 1) {
                    //You must kill 3 more Adjutants to make Commander Bakarma appear.
                    sendMsgByRace(1400758, Race.PC_ALL, 0);
                } else if (adjutantsKilled == 2) {
                    //You must kill 2 more Adjutants to make Commander Bakarma appear.
                    sendMsgByRace(1400759, Race.PC_ALL, 0);
                } else if (adjutantsKilled == 3) {
                    //You must kill 1 more Adjutant to make Commander Bakarma appear.
                    sendMsgByRace(1400760, Race.PC_ALL, 0);
                } else if (adjutantsKilled == 4) {
                    spawnCommanderBakarma();
                    //Commander Bakarma has appeared at Beritra's Oracle.
                    sendMsgByRace(1400751, Race.PC_ALL, 0);
                    deleteNpc(214026); //Deputy Brigade General Yavant.
                }
                break;
            case 213780: //Commander Bakarma.
               // sendMsg(MuiService.getInstance().getMessage("STR_SEND_MSG_FINISH_INSTANCE", WorldMapType.DRAUPNIR_CAVE.getEnumName()));
                switch (Rnd.get(1, 2)) {
                    case 1:
                        spawn(702658, 787.32513f, 431.49173f, 319.62155f, (byte) 33); //Abbey Box.
                        break;
                    case 2:
                        spawn(702659, 787.32513f, 431.49173f, 319.62155f, (byte) 33); //Noble Abbey Box.
                        break;
                }
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        spawnAkhal();
                        //A powerful Balaur has appeared in Beritra's Oracle Chamber.
                        sendMsgByRace(1403068, Race.PC_ALL, 0);
                    }
                }, 60000);
                break;
            case 236900: //Bakarma Charger.
                bakarmaCharger++;
                if (bakarmaCharger == 18) {
                    abyssGateTask.cancel(true);
                    //The Abyss Gate Enhancer has been neutralized.
                    sendMsgByRace(1403065, Race.PC_ALL, 0);
                }
                break;
        }
    }

    /**
     * Central Control Room Raid.
     */
    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
            case 702857: //Balaur Abyss Gate Enhancer.
                despawnNpc(npc);
                //Balaur are swarming to defend the Abyss Gate Enhancer.
                sendMsgByRace(1403063, Race.PC_ALL, 0);
                //The Balaur have been alerted to the presence of intruders.
                sendMsgByRace(1403064, Race.PC_ALL, 4000);
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        startAbyssGateRaid1();
                    }
                }, 5000);
                abyssGateTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        //Balaur are swarming to defend the Abyss Gate Enhancer.
                        sendMsgByRace(1403063, Race.PC_ALL, 0);
                        startAbyssGateRaid2();
                    }
                }, 60000);
                break;
            case 702858: //Balaur Abyss Gate Booster.
                despawnNpc(npc);
                //Find and overload the Abyss Gate Enhancer in the Central Control Room.
                sendMsgByRace(1403058, Race.PC_ALL, 0);
                //The Balaur's Abyss Gate Enhancer is active.
                //The enhancer protection device will activate in 3 minutes, preventing it from being destroyed.
                sendMsgByRace(1403081, Race.PC_ALL, 5000);
                spawn(702857, 469.00000f, 563.0000f, 510.49686f, (byte) 29); //Balaur Abyss Gate Enhancer.
                spawn(702857, 511.36166f, 591.0183f, 510.60300f, (byte) 60); //Balaur Abyss Gate Enhancer.
                spawn(702857, 466.00000f, 617.0000f, 511.22543f, (byte) 96); //Balaur Abyss Gate Enhancer.
                break;
        }
    }

    private void spawnCommanderBakarma() {
        spawn(213780, 777.46985f, 431.09888f, 321.7541f, (byte) 62); //Commander Bakarma.
    }

    private void spawnAkhal() {
        spawn(237275, 777.46985f, 431.09888f, 321.7541f, (byte) 62); //Akhal.
    }

    public void startAbyssGateRaid1() {
        abyssGateRaid((Npc) spawn(236900, 514.45465f, 614.66077f, 515.35785f, (byte) 67));
        abyssGateRaid((Npc) spawn(236900, 514.45465f, 614.66077f, 515.35785f, (byte) 67));
        abyssGateRaid((Npc) spawn(236900, 514.45465f, 614.66077f, 515.35785f, (byte) 67));
    }

    public void startAbyssGateRaid2() {
        abyssGateRaid((Npc) spawn(236900, 514.45465f, 614.66077f, 515.35785f, (byte) 67));
        abyssGateRaid((Npc) spawn(236900, 514.45465f, 614.66077f, 515.35785f, (byte) 67));
        abyssGateRaid((Npc) spawn(236900, 514.45465f, 614.66077f, 515.35785f, (byte) 67));
    }

    private void abyssGateRaid(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    for (Player player : instance.getPlayersInside()) {
                        npc.setTarget(player);
                        ((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
                        npc.setState(1);
                        npc.getMoveController().moveToTargetObject();
                        PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
                    }
                }
            }
        }, 1000);
    }

    protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

    private void deleteNpc(int npcId) {
        if (getNpc(npcId) != null) {
            getNpc(npcId).getController().onDelete();
        }
    }

    protected void sendMsgByRace(final int msg, final Race race, int time) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                instance.doOnAllPlayers(new Visitor<Player>() {
                    @Override
                    public void visit(Player player) {
                        if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
                            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
                        }
                    }
                });
            }
        }, time);
    }
}
