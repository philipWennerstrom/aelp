package com.aionemu.gameserver.dataholders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.fix.drops.NpcDropsFix;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.npcdrops.XmlDrop;
import com.aionemu.gameserver.model.npcdrops.XmlDropGroup;
import com.aionemu.gameserver.model.npcdrops.XmlNpcDrops;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

/**
 * @author MrPoke

@XmlRootElement(name = "npc_drops")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "npcDropData", propOrder = { "npcDrop" }) */
public class NpcDropData {
	public static final String GODSTONES = "GODSTONES";
	public static final Map<Integer, Drop> godstoneDrops = new HashMap<Integer, Drop>();
	public static final Map<Integer, Drop> decorationDrops = new HashMap<Integer, Drop>();
	private static Logger log = LoggerFactory.getLogger(DataManager.class);
    //@XmlElement(name = "npc_drop")
    protected List<NpcDrop> npcDrop;
    
    public NpcDropData() {}
    
    public NpcDropData (NpcDropData data) {
    	npcDrop = data.getNpcDrop();
    }
    /**
     * @return the npcDrop
     */
    public List<NpcDrop> getNpcDrop() {
        return npcDrop;
    }

    /**
     * @param npcDrop
     *          the npcDrop to set
     */
    public void setNpcDrop(List<NpcDrop> npcDrop) {
        this.npcDrop = npcDrop;
    }

    public int size() {
        return npcDrop.size();
    }
    
    @SuppressWarnings("resource")
   	public static NpcDropData load() {
   		List<Drop> drops = new ArrayList<Drop>();
   		List<String> names = new ArrayList<String>();
   		final List<NpcDrop> npcDrops = new ArrayList<NpcDrop>();
   		FileChannel roChannel = null;
   		HashMap<Integer, ArrayList<DropGroup>> xmlGroup = DataManager.XML_NPC_DROP_DATA.getDrops();
   		try {
   			final RandomAccessFile channel = new RandomAccessFile("data/static_data/npc_drops/npc_drop.dat", "r");
   			roChannel = channel.getChannel();
   			final int size = (int) roChannel.size();
   			final MappedByteBuffer buffer = roChannel.map(FileChannel.MapMode.READ_ONLY, 0L, size).load();
   			buffer.order(ByteOrder.LITTLE_ENDIAN);
   			for (int count = buffer.getInt(), i = 0; i < count; ++i) {
   				drops.add(Drop.load(buffer));
   			}
   			for (int count = buffer.getInt(), i = 0; i < count; ++i) {
   				final int lenght = buffer.get();
   				final byte[] byteString = new byte[lenght];
   				buffer.get(byteString);
   				final String name = new String(byteString);
   				names.add(name);
   			}
   			for (int count = buffer.getInt(), i = 0; i < count; ++i) {
   				final int npcId = buffer.getInt();
   				final int groupCount = buffer.getInt();
   				final List<DropGroup> dropGroupList = new ArrayList<DropGroup>(groupCount);
   				final List<Map<Integer, Drop>> dropGroupMap = new ArrayList<Map<Integer,Drop>>();
   				ArrayList<DropGroup> npcXmlGroup = xmlGroup.get(npcId);
				for (int groupIndex = 0; groupIndex < groupCount; ++groupIndex) {
   					Map<Integer, Drop> drl = new HashMap<Integer, Drop>();
   					final byte raceId = buffer.get();
   					Race race = null;
   					switch (raceId) {
   						case 0: {
   							race = Race.ELYOS;
   							break;
   						}
   						case 1: {
   							race = Race.ASMODIANS;
   							break;
   						}
   						default: {
   							race = Race.PC_ALL;
   							break;
   						}
   					}
   					final boolean useCategory = buffer.get() == 1;
   					final String groupName = names.get(buffer.getShort());
   					final int dropCount = buffer.getInt();
   					final List<Drop> dropList = new ArrayList<Drop>();
   					for (int dropIndex = 0; dropIndex < dropCount; ++dropIndex) {
   						Drop e = drops.get(buffer.getInt());
   						dropList.add(e);
						drl.put(Integer.valueOf(e.getItemId()), e);
   					}
   					final DropGroup dropGroup = new DropGroup(dropList, race, useCategory, groupName);
   					dropGroupList.add(dropGroup);
   					dropGroupMap.add(drl);
   				}
   				if (npcXmlGroup != null) {
   					dropGroupList.addAll(npcXmlGroup);
   					xmlGroup.remove(npcId);
   				}
   				final NpcDrop npcDrop = new NpcDrop(dropGroupList, npcId);
   				npcDrops.add(npcDrop);
   				final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
   				if (npcTemplate != null) {
   					npcTemplate.setNpcDrop(npcDrop);
   				}
   			}
   			if (!xmlGroup.isEmpty()) {
   				for (final Map.Entry<Integer, ArrayList<DropGroup>> entry : xmlGroup.entrySet()) {
   					final NpcDrop npcDrop2 = new NpcDrop(entry.getValue(), entry.getKey());
   					npcDrops.add(npcDrop2);
   					final NpcTemplate npcTemplate2 = DataManager.NPC_DATA.getNpcTemplate(entry.getKey());
   					if (npcTemplate2 != null) {
   						npcTemplate2.setNpcDrop(npcDrop2);
   					}
   				}
   			}
   			drops.clear();
   			drops = null;
   			names.clear();
   			names = null;
   			xmlGroup.clear();
   			xmlGroup = null;
   			DataManager.XML_NPC_DROP_DATA.clear();
   		}
   		catch (FileNotFoundException e) {
   			NpcDropData.log.error("Drop loader: Missing npc_drop.dat!!! carregando apenas xml data");
   			for(XmlNpcDrops xmlDrops: DataManager.XML_NPC_DROP_DATA.getNds()) {
   				int npcId = xmlDrops.getNpcId();
   				final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
   				List<DropGroup> newDg = new ArrayList<DropGroup>();
   				for (XmlDropGroup dg : xmlDrops.getDropGroup()) {
   					List<Drop> dr = new ArrayList<Drop>();
   					for (XmlDrop xd : dg.getDrop()) {
   						Drop datDg = new Drop(xd.getItemId(), xd.getMinAmount(), xd.getMaxAmount(), xd.getChance(), xd.isNoReduction(), xd.isEachMember());
   						dr.add(datDg);
   					}
   					DropGroup datDg = new DropGroup(dr, dg.getRace(), dg.isUseCategory(), dg.getGroupName());
   					newDg.add(datDg);
   				}
   				final NpcDrop npcDrop = new NpcDrop(newDg, npcId);
   				if (npcTemplate != null) {
   					npcTemplate.setNpcDrop(npcDrop);
   				}
   			}
   		}
   		catch (IOException e2) {
   			NpcDropData.log.error("Drop loader: IO error in drop Loading.");
   		}
   		finally {
   			try {
   				if (roChannel != null) {
   					roChannel.close();
   				}
   			}
   			catch (IOException e3) {
   				NpcDropData.log.error("Drop loader: IO error in drop Loading.");
   			}
   		}
   		final NpcDropData dropData = new NpcDropData();
   		NpcDropsFix.fixRates(npcDrops);
   		NpcDropData.log.info("Drop loader: Npc drops loading done. Total loaded: "+ npcDrops.size());
		for (NpcDrop npcDrop2 : npcDrops) {
			for (DropGroup dropGroup : npcDrop2.getDropGroup()) {
				if (dropGroup.getGroupName().equals(GODSTONES)) {
					for (Drop godDrop : dropGroup.getDrop()) {
						int itemId = godDrop.getItemId();
						if (!godstoneDrops.containsKey(itemId)) {
							godstoneDrops.put(itemId, godDrop);
						}
					}
				}
			}
		}
		
		for (NpcDrop npcDrop2 : npcDrops) {
			for (DropGroup dropGroup : npcDrop2.getDropGroup()) {

				for (Drop godDrop : dropGroup.getDrop()) {
					
					if (godDrop.getItemId()==186000079) {
						decorationDrops.put(186000079, godDrop);
					}
					
					if (godDrop.getItemId()==186000078) {
						decorationDrops.put(186000078, godDrop);
					}
					
					int itemId = godDrop.getItemId();
					if (isReliquia(itemId)) {
						if (godDrop.getChance() > 0.4) {
							float maxItens = (Math.random() <= 0.5) ? 0.7f : 0.15f;
							godDrop.setChance(maxItens);
						}
						System.out.println("Item: " + itemId + "name = " + godDrop.getItemTemplate().getName()
								+ " Chance: " + godDrop.getChance());
					}
				}
			
			}
		}
   		dropData.setNpcDrop(npcDrops);
   		return dropData;
   	}

	public static boolean isReliquia(int itemId) {
		return (itemId > 186000048 && itemId < 186000097) || itemId==186000030;
	}
       
       public static void reload() {
   		TIntObjectHashMap<NpcTemplate> npcData = DataManager.NPC_DATA.getNpcData();
   		npcData.forEachValue(new TObjectProcedure<NpcTemplate>() {

   			@Override
   			public boolean execute(NpcTemplate npcTemplate) {
   				npcTemplate.setNpcDrop(null);
   				return false;
   			}
   		});
   		load();
   	}
    
/**@SuppressWarnings("resource")
public static NpcDropData load(List<NpcDrop> staticDrops) {
		List<Drop> drops = new ArrayList<Drop>();
		List<String> names = new ArrayList<String>();
		List<NpcDrop> npcDrops = new ArrayList<NpcDrop>();
		FileChannel roChannel = null;
		MappedByteBuffer buffer;

			try {
				roChannel = new RandomAccessFile("data/static_data/npc_drops/npc_drop.dat", "r").getChannel();
				int size = (int) roChannel.size();
				buffer = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				int count = buffer.getInt();
				for (int i = 0; i<count; i++){
					drops.add(Drop.load(buffer));
				}
				
				count = buffer.getInt();
				
				for (int i = 0; i<count; i++){
					int lenght = buffer.get();
					byte[] byteString = new byte[lenght];
					buffer.get(byteString);
					String name = new String(byteString);
					names.add(name);
				}
				
				count = buffer.getInt();
				for (int i = 0; i<count; i++){
					int npcId = buffer.getInt();

					int groupCount = buffer.getInt();
					List<DropGroup> dropGroupList = new ArrayList<DropGroup>(groupCount);
					for (int groupIndex = 0; groupIndex<groupCount; groupIndex++){
							Race race;
							byte raceId = buffer.get();
							switch(raceId){
								case 0:
									race = Race.ELYOS;
									break;
								case 1:
									race = Race.ASMODIANS;
									break;
								default:
									race = Race.PC_ALL;
							}
							
							boolean useCategory = buffer.get() == 1 ? true:false;
							String groupName = names.get(buffer.getShort());
							
							int dropCount = buffer.getInt();
							List<Drop> dropList = new ArrayList<Drop>(dropCount);
							for (int dropIndex = 0; dropIndex < dropCount; dropIndex++){
								dropList.add(drops.get(buffer.getInt()));
							}
							DropGroup dropGroup = new DropGroup(dropList, race, useCategory, groupName);
							dropGroupList.add(dropGroup);
					}
					NpcDrop npcDrop = new NpcDrop(dropGroupList, npcId);
					npcDrops.add(npcDrop);

					NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
					if (npcTemplate != null){
						npcTemplate.setNpcDrop(npcDrop);
					}
				}
				drops.clear();
				drops = null;
				names.clear();
				names = null;
			}
			catch (FileNotFoundException e) {
				log.error("Drop loader: Missing npc_drop.dat!!!");
				log.error("Drop loader: Only static drops are being used!!!");
			}
			catch (IOException e) {
				log.error("Drop loader: IO error in drop Loading.");
			}
			finally{
				try {
					if (roChannel != null)
						roChannel.close();
					
				}
				catch (IOException e) {
					log.error("Drop loader: IO error in drop Loading.");
				}
			}
				
		for (NpcDrop staticDrop : staticDrops) {
			npcDrops.add(staticDrop);
		}
		
		NpcDropData dropData = new NpcDropData();
		NpcDropsFix.fixRates(npcDrops);
		dropData.setNpcDrop(npcDrops);
		
		log.info("Drop loader: Npc drops loading done: " + npcDrops.size() +" drops.");
		return dropData;
	}

	private static void forEachFileDataGroupDrop(NpcDrop dataFileNpcDrop, NpcDrop staticNpcDrop) {
		for (DropGroup staticDp:staticNpcDrop.getDropGroup()) {
			for(DropGroup dp : dataFileNpcDrop.getDropGroup()) {
				
				for (Drop staticDrop : staticDp.getDrop()) {
					for(Drop drop : dp.getDrop()) {
						boolean addToDp = true;
						if(staticDrop.getItemId()!=drop.getItemId()) {
							dp.getDrop().add(staticDrop);
							addToDp= false;
						}
						if(!addToDp) {break;}
					}
				}
			}
			
		}
	}
	
	
	
	public static void reload(List<NpcDrop> staticDrop){
		TIntObjectHashMap<NpcTemplate> npcData = DataManager.NPC_DATA.getNpcData();
		npcData.forEachValue(new TObjectProcedure<NpcTemplate>(){

			@Override
			public boolean execute(NpcTemplate npcTemplate) {
				npcTemplate.setNpcDrop(null);
				return false;
			}
		});
		load(staticDrop);
	}*/
}