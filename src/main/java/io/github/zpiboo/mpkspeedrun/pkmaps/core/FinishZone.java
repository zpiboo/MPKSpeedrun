package io.github.zpiboo.mpkspeedrun.pkmaps.core;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.zpiboo.mpkspeedrun.Speedrunner;
import org.json.JSONObject;

public class FinishZone extends TriggerZone {
    private long lastLandTick = -1;

    @Override
    public void tick(Player p, Speedrunner s) {
        if (p.landTick)
            lastLandTick = API.tickTime;

        super.tick(p, s);
    }

    @Override
    protected void onTrigger(Player p, Speedrunner s) {
        super.onTrigger(p, s);

        getLastTrigger().setTickIndicator(
                p.isOnGround()
                        ? (int) (API.tickTime - lastLandTick)
                        : -p.getAirtime()
        );
    }

    public static FinishZone fromJson(JSONObject boxJson) {
        FinishZone newZone = new FinishZone();
        newZone.applyJson(boxJson);

        return newZone;
    }
}