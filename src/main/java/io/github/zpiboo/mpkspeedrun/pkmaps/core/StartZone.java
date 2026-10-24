package io.github.zpiboo.mpkspeedrun.pkmaps.core;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.zpiboo.mpkspeedrun.Speedrunner;
import org.json.JSONObject;

public class StartZone extends TriggerZone {
    private TriggerState lastTriggerState = TriggerState.NONE;
    private long lastTriggerTime = -1;

    @Override
    public boolean countTriggerTickDefault() {
        return true;
    }

    @Override
    public void tick(Player p, Speedrunner s) {
        super.tick(p, s);

        if (lastTriggerState == TriggerState.GROUNDED) {
            if (!p.isOnGround()) {
                lastTriggerState = TriggerState.NONE;
            } else {
                getLastTrigger().setTickIndicator((int) (API.tickTime - lastTriggerTime));  // positive
            }
        } else if (lastTriggerState == TriggerState.AIRBORNE) {
            if (p.isOnGround()) {
                lastTriggerState = TriggerState.NONE;
            }
            getLastTrigger().setTickIndicator((int) (lastTriggerTime - API.tickTime));  // negative
        }
    }

    @Override
    protected void onTrigger(Player p, Speedrunner s) {
        super.onTrigger(p, s);

        lastTriggerState = p.isOnGround()
                ? TriggerState.GROUNDED
                : TriggerState.AIRBORNE;

        lastTriggerTime = API.tickTime;
    }

    public static StartZone fromJson(JSONObject boxJson) {
        StartZone newZone = new StartZone();
        newZone.applyJson(boxJson);

        return newZone;
    }
}