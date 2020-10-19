package pirate.events.holders;

import pirate.events.enums.EventPlayerLevel;
import pirate.events.enums.EventType;

/**
 *
 * @author f14shm4n
 */
public class MultiPlayerEventHolder extends SimpleSinglePlayerEventHolder {

    public MultiPlayerEventHolder(int index, EventType etype, EventPlayerLevel epl) {
		super(index, etype, epl);
	}

	/**@Override
    public boolean isReadyToGo() {
        return this.allPlayers.size() >= 1;
    }**/
}