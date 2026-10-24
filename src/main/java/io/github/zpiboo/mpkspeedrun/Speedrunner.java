package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.events.OnTickEndEvent;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.PkMap;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.TriggerZone;

public class Speedrunner {
    @InfoString.AccessInstance
    public static final Speedrunner INSTANCE = new Speedrunner();

    private int groundtime = 0;
    private int runTicks = 0;

    private TriggerZone.TriggerData lastTrigger = new TriggerZone.TriggerData();

    private boolean isMoving = false;

    private PkMap currentMap = null;
    private final Timer timer = new Timer();


    @InfoString.Getter
    public int getGroundtime() {
        return groundtime;
    }

    @InfoString.Getter
    public int getRunTicks() {
        return runTicks;
    }

    @InfoString.Getter
    public TriggerZone.TriggerData getLastTrigger() {
        return lastTrigger;
    }
    public void setLastTrigger(TriggerZone.TriggerData lastTrigger) {
        this.lastTrigger = lastTrigger;
    }

    @InfoString.Getter
    public Timer getTimer() {
        return timer;
    }


    @InfoString.Getter
    public PkMap getCurrentMap() {
        return currentMap != null
                ? currentMap
                : PkMap.NONE;
    }
    public void setCurrentMap(PkMap map) {
        currentMap = map;
        lastTrigger = new TriggerZone.TriggerData();
        timer.reset();
        timer.setEnabled(false);
    }


    public void onTickEnd(@SuppressWarnings("unused") OnTickEndEvent evt) {
        Player currPlayer = Player.getLatest();
        if (currPlayer == null) return;

        final PkMap pkMap = getCurrentMap();
        if (pkMap != null) pkMap.tick(currPlayer, this);

        Player prevPlayer = currPlayer.getPrevious();
        if (prevPlayer == null) return;

        boolean wasMoving = isMoving;
        isMoving = !currPlayer.keyInput.getMovementVector().equals(Vector2D.ZERO);

        boolean isOnGround = currPlayer.isOnGround();
        boolean wasOnGround = prevPlayer.isOnGround();

        if (isOnGround) {
            if (wasOnGround)
                groundtime++;
            else
                groundtime = 0;
        }

        Player pprevPlayer = prevPlayer.getPrevious();
        if (pprevPlayer == null) return;

        boolean wasWasOnGround = pprevPlayer.isOnGround();

        if (wasOnGround) {
            if (isMoving) {
                if (wasMoving)
                    runTicks++;
                if (!wasWasOnGround || !wasMoving)
                    runTicks = 0;
            } else if (wasWasOnGround && wasMoving) {
                runTicks++;
            }
        }
    }
}
