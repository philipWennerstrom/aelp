package com.aionemu.gameserver.dataholders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.fix.drops.NpcDropsFix;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

/**
 * @author MrPoke
 */
@XmlRootElement(name = "npc_drops")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "npcDropData", propOrder = { "npcDrop" })
public class NpcDropData {

	private static Logger log = LoggerFactory.getLogger(DataManager.class);
    @XmlElement(name = "npc_drop")
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
			
		
		
		/**for(NpcDrop staticDrop: staticDrops) {
			boolean addStDrop = true;
			for(NpcDrop dataFileDrop: npcDrops) {
				
				if (staticDrop.getNpcId() == dataFileDrop.getNpcId()) {
					addStDrop = false;
					
					forEachFileDataGroupDrop(dataFileDrop, staticDrop);
					
					break;
				}
					
			}
			if(addStDrop) {
				npcDrops.add(staticDrop);
			}
		}**/
			
			
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
	}
}