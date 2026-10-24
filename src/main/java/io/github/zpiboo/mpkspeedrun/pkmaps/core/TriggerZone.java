package io.github.zpiboo.mpkspeedrun.pkmaps.core;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.MPKSpeedrun;
import io.github.zpiboo.mpkspeedrun.Speedrunner;
import io.github.zpiboo.mpkspeedrun.util.api_compat.BoundingBox3DUtil;
import io.github.zpiboo.mpkspeedrun.util.api_compat.PlayerUtil;
import io.github.zpiboo.mpkspeedrun.util.misc.ChoiceEnum;
import org.json.JSONObject;

@InfoString.DataClass
public class TriggerZone {
    private BoundingBox3D box;
    private TriggerMode triggerMode;
    private PosMode posMode;
    private boolean useLandingPos;
    private boolean countTriggerTick;

    private boolean didTrigger = false;
    protected final TriggerData lastTrigger = new TriggerData();

    @SuppressWarnings("unused") public static final TriggerZone ZERO = new TriggerZone();

    public enum TriggerMode implements ChoiceEnum<TriggerMode> {
        ENTER, EXIT
    }
    public enum PosMode implements ChoiceEnum<PosMode> {
        POS, BOX
    }

    public TriggerZone(BoundingBox3D box, TriggerMode triggerMode, PosMode posMode, boolean useLandingPos) {
        this.box = new BoundingBox3D(box.getMin(), box.getMax());
        this.triggerMode = triggerMode;
        this.posMode = posMode;
        this.useLandingPos = useLandingPos;
        this.countTriggerTick = countTriggerTickDefault();
    }
    public TriggerZone() {
        this(BoundingBox3D.ZERO, TriggerMode.ENTER, PosMode.POS, false);
    }

    @InfoString.Getter
    public BoundingBox3D getBox() {
        return box;
    }
    @SuppressWarnings("unused")
    public void setBox(BoundingBox3D box) {
        this.box = box;
    }

    @InfoString.Getter
    public TriggerMode getTriggerMode() {
        return triggerMode;
    }
    public void setTriggerMode(TriggerMode triggerMode) {
        this.triggerMode = triggerMode;
    }

    @InfoString.Getter
    public PosMode getPosMode() {
        return posMode;
    }
    public void setPosMode(PosMode posMode) {
        this.posMode = posMode;
    }

    @InfoString.Getter
    public boolean isUsingLandingPos() {
        return useLandingPos;
    }
    public void setUsingLandingPos(boolean useLandingPos) {
        this.useLandingPos = useLandingPos;
    }

    @InfoString.Getter
    public boolean countTriggerTick() {
        return countTriggerTick;
    }
    public void setCountTriggerTick(boolean countTriggerTick) {
        this.countTriggerTick = countTriggerTick;
    }

    public boolean countTriggerTickDefault() {
        return false;
    }


    public boolean didTrigger() {
        return didTrigger;
    }
    @InfoString.Getter
    public TriggerData getLastTrigger() {
        return lastTrigger;
    }


    private Vector3D getAdaptedPos(Player p) {
        return useLandingPos
                ? PlayerUtil.getLandingPos(p)
                : p.getPos();
    }
    private BoundingBox3D getAdaptedBB(Player p) {
        return useLandingPos
                ? PlayerUtil.getLandingBB(p)
                : p.getBoundingBox();
    }

    private double calculateSubtick(Player p) {
        switch (posMode) {
            case POS:
                Vector3D lastPos = getAdaptedPos(p.getPrevious());
                Vector3D currPos = getAdaptedPos(p);
                switch (triggerMode) {
                    case ENTER:
                        return BoundingBox3DUtil.posSubtick(
                                lastPos,
                                currPos,
                                this.getBox()
                        );
                    case EXIT:
                        return 1.0D - BoundingBox3DUtil.posSubtick(
                                currPos,
                                lastPos,
                                this.getBox()
                        );

                    default: break;
                }
            case BOX:
                BoundingBox3D lastBB = getAdaptedBB(p.getPrevious());
                BoundingBox3D currBB = getAdaptedBB(p);
                switch (triggerMode) {
                    case ENTER:
                        return BoundingBox3DUtil.boxSubtick(
                                lastBB,
                                currBB,
                                this.getBox()
                        );
                    case EXIT:
                        return 1.0D - BoundingBox3DUtil.boxSubtick(
                                currBB,
                                lastBB,
                                this.getBox()
                        );

                    default: break;
                }

            default: return 0.0D;
        }
    }

    private boolean shouldTrigger(Player p) {
        Vector3D currPos = getAdaptedPos(p);
        Vector3D lastPos = getAdaptedPos(p.getPrevious());
        BoundingBox3D currBb = getAdaptedBB(p);
        BoundingBox3D lastBb = getAdaptedBB(p.getPrevious());

        switch (triggerMode) {
            case ENTER:
            switch (posMode) {
                case POS:  // POS ENTER
                return BoundingBox3DUtil.contains(box, currPos) && !BoundingBox3DUtil.contains(box, lastPos);

                case BOX:  // BOX ENTER
                return BoundingBox3DUtil.intersect(box, currBb) && !BoundingBox3DUtil.intersect(box, lastBb);
            }

            case EXIT:
            switch (posMode) {
                case POS:  // POS EXIT
                return BoundingBox3DUtil.contains(box, lastPos) && !BoundingBox3DUtil.contains(box, currPos);

                case BOX:  // BOX EXIT
                return BoundingBox3DUtil.intersect(box, lastBb) && !BoundingBox3DUtil.intersect(box, currBb);
            }

            default: return false;
        }
    }

    public void tick(Player p, Speedrunner s) {
        didTrigger = shouldTrigger(p);

        if (didTrigger)
            onTrigger(p, s);
    }

    protected void onTrigger(Player p, Speedrunner s) {
        s.setLastTrigger(this.lastTrigger);

        double subtick = calculateSubtick(p);
        lastTrigger.setSubtick(subtick);

        Vector3D lastPos = getAdaptedPos(p.getPrevious());
        Vector3D currPos = getAdaptedPos(p);
        double totalDistance = currPos.sub(lastPos).lengthXZ();

        lastTrigger.setMadeByOffset(totalDistance * (1 - subtick));
        lastTrigger.setMissedByOffset(totalDistance * subtick);
    }


    public JSONObject toJson() {
        return new JSONObject()
                .put("trigger_on", triggerMode.toString())
                .put("pos_mode", posMode.toString())
                .put("use_landing_pos", useLandingPos)
                .put("count_trigger_tick", countTriggerTick)

                .put("minx", box.minX())
                .put("miny", box.minY())
                .put("minz", box.minZ())

                .put("maxx", box.maxX())
                .put("maxy", box.maxY())
                .put("maxz", box.maxZ());
    }
    public static TriggerZone fromJson(JSONObject boxJson) {
        TriggerZone newZone = new TriggerZone();
        newZone.applyJson(boxJson);

        return newZone;
    }

    protected void applyJson(JSONObject boxJson) {
        String triggerModeString = boxJson.optString("trigger_on");
        String posModeString = boxJson.optString("pos_mode");

        TriggerMode triggerMode;
        PosMode posMode;

        try {
            triggerMode = TriggerMode.valueOf(triggerModeString);
        } catch (IllegalArgumentException e) {
            triggerMode = TriggerMode.ENTER;
            MPKSpeedrun.LOGGER.warn("Trigger mode not found: " + triggerModeString);
        }
        try {
            posMode = PosMode.valueOf(posModeString);
        } catch (IllegalArgumentException e) {
            posMode = PosMode.POS;
            MPKSpeedrun.LOGGER.warn("Position mode not found: " + posModeString);
        }

        boolean useLandingPos = boxJson.optBoolean("use_landing_pos", false);
        boolean countTriggerTick = boxJson.optBoolean("count_trigger_tick", countTriggerTickDefault());

        double minX = boxJson.optDouble("minx", 0);
        double minY = boxJson.optDouble("miny", 0);
        double minZ = boxJson.optDouble("minz", 0);
        double maxX = boxJson.optDouble("maxx", 0);
        double maxY = boxJson.optDouble("maxy", 0);
        double maxZ = boxJson.optDouble("maxz", 0);

        BoundingBox3D box = new BoundingBox3D(
                new Vector3D(minX, minY, minZ),
                new Vector3D(maxX, maxY, maxZ)
        );

        this.setBox(box);
        this.setTriggerMode(triggerMode);
        this.setPosMode(posMode);
        this.setUsingLandingPos(useLandingPos);
        this.setCountTriggerTick(countTriggerTick);
    }

    protected enum TriggerState {
        GROUNDED, AIRBORNE, NONE
    }

    @InfoString.DataClass
    public static class TriggerData {
        private int tickIndicator = 0;
        private double subtick = 0.0D;
        private double madeByOffset = 0.0D;
        private double missedByOffset = 0.0D;

        @InfoString.Getter
        public int getTickIndicator() {
            return tickIndicator;
        }
        public void setTickIndicator(int tickIndicator) {
            this.tickIndicator = tickIndicator;
        }

        @InfoString.Getter
        public double getSubtick() {
            return subtick;
        }
        public void setSubtick(double subtick) {
            this.subtick = subtick;
        }

        @InfoString.Getter
        public double getMadeByOffset() {
            return madeByOffset;
        }
        public void setMadeByOffset(double offset) {
            this.madeByOffset = offset;
        }

        @InfoString.Getter
        public double getMissedByOffset() {
            return missedByOffset;
        }
        public void setMissedByOffset(double offset) {
            this.missedByOffset = offset;
        }
    }
}
