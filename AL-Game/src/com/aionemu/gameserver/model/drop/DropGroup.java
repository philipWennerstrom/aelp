package com.aionemu.gameserver.model.drop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dropGroup", propOrder = { "drop" })
public class DropGroup implements DropCalculator {

    protected List<Drop> drop;
    @XmlAttribute
    protected Race race = Race.PC_ALL;
    @XmlAttribute(name = "use_category")
    protected Boolean useCategory = true;
    @XmlAttribute(name = "name")
    protected String group_name;
    
    public DropGroup() {
    	
    }
    
    public DropGroup(DropGroup dropGroup) {
    	this.drop= new ArrayList<Drop>();
    	for(Drop dp:dropGroup.getDrop()) {
    		drop.add(new Drop(dp.getItemId(), dp.getMinAmount(), dp.getMaxAmount(), dp.getChance(), dp.isNoReduction()));
    	}
    	this.race = dropGroup.getRace();
		this.useCategory = dropGroup.useCategory;
		this.group_name = dropGroup.group_name;
    }
    
    
    
    public void setDrop(List<Drop> drop) {
		this.drop = drop;
	}

	/**
	 * @param drop
	 * @param race
	 * @param useCategory
	 * @param group_name
	 */
	public DropGroup(List<Drop> drop, Race race, Boolean useCategory, String group_name) {
		this.drop = drop;
		this.race = race;
		this.useCategory = useCategory;
		this.group_name = group_name;
	}

    public List<Drop> getDrop() {
        return this.drop;
    }

    public Race getRace() {
        return race;
    }

    public Boolean isUseCategory() {
        return useCategory;
    }

    /**
     * @return the name
     */
    public String getGroupName() {
        if (group_name == null)
            return "";
        return group_name;
    }

    @Override
    public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, Collection<Player> groupMembers) {
        if (useCategory) {
            Drop d = drop.get(Rnd.get(0, drop.size() - 1));
            return d.dropCalculator(result, index, dropModifier, race, groupMembers);
        }

        for (int i = 0; i < drop.size(); i++) {
            Drop d = drop.get(i);
            index = d.dropCalculator(result, index, dropModifier, race, groupMembers);
        }
        return index;
    }
}