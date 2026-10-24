package io.github.zpiboo.mpkspeedrun.pkmaps.gui.editor;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.FontRenderer;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.gui.components.Anchor;
import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.gui.components.CheckButton;
import io.github.kurrycat.mpkmod.gui.components.Div;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.TriggerZone;
import io.github.zpiboo.mpkspeedrun.util.components.ChoiceButton;

import java.awt.*;

public class TriggerZoneEditor extends Div {
    private final TriggerZone triggerZone;

    private final String label;

    private final Button currentBlockButton;
    private final Button currentPosButton;
    private final BB3DEditor bbEditor;
    private final ChoiceButton<TriggerZone.TriggerMode> triggerModeChoice;
    private final ChoiceButton<TriggerZone.PosMode> posModeChoice;
    private final CheckButton landingPosCheckbox;
    private final CheckButton countTickCheckbox;

    public TriggerZoneEditor(TriggerZone triggerZone, String label, Vector2D pos, double width) {
        this.triggerZone = triggerZone;
        this.label = label;

        double heightUpToHere = 0;
        currentBlockButton = new Button("Set Block Box", new Vector2D(0, heightUpToHere), new Vector2D(0.22D, 11));
        addChild(currentBlockButton, PERCENT.SIZE_X, Anchor.TOP_RIGHT);

        currentPosButton = new Button("Set Pos Box", new Vector2D(0.26D, heightUpToHere), new Vector2D(0.22D, 11));
        addChild(currentPosButton, PERCENT.X, Anchor.TOP_RIGHT);

        heightUpToHere += 16;
        bbEditor = new BB3DEditor(triggerZone.getBox(), new Vector2D(0, heightUpToHere), 1.00D);
        addChild(bbEditor, PERCENT.SIZE_X);

        currentBlockButton.setButtonCallback(mouseButton -> {
            Player player = Player.getLatest();
            if (player == null) return;

            Vector3D floorPos = player.getPos().floor();
            BoundingBox3D blockBB = new BoundingBox3D(floorPos, floorPos.add(1));

            bbEditor.setFieldsForBB(blockBB);
        });
        currentPosButton.setButtonCallback(mouseButton -> {
            Player player = Player.getLatest();
            if (player == null) return;

            Vector3D playerPos = player.getPos();
            BoundingBox3D posBB = new BoundingBox3D(playerPos, playerPos);

            bbEditor.setFieldsForBB(posBB);
        });

        heightUpToHere += bbEditor.getDisplayedSize().getY() + 5;
        triggerModeChoice = new ChoiceButton<>(TriggerZone.TriggerMode.class, new Vector2D(0, heightUpToHere), new Vector2D(0.48D, 11));
        triggerModeChoice.setCurrent(triggerZone.getTriggerMode());
        addChild(triggerModeChoice, PERCENT.SIZE_X, Anchor.TOP_LEFT);

        posModeChoice = new ChoiceButton<>(TriggerZone.PosMode.class, new Vector2D(0, heightUpToHere), new Vector2D(0.48D, 11));
        posModeChoice.setCurrent(triggerZone.getPosMode());
        addChild(posModeChoice, PERCENT.SIZE_X, Anchor.TOP_RIGHT);

        heightUpToHere += triggerModeChoice.getDisplayedSize().getY() + 5;
        landingPosCheckbox = new CheckButton(new Vector2D(0, heightUpToHere));
        landingPosCheckbox.setChecked(triggerZone.isUsingLandingPos());
        addChild(landingPosCheckbox, PERCENT.NONE, Anchor.TOP_LEFT);
        countTickCheckbox = new CheckButton(new Vector2D(0.52D, heightUpToHere));
        countTickCheckbox.setChecked(triggerZone.countTriggerTick());
        addChild(countTickCheckbox, PERCENT.POS_X, Anchor.TOP_LEFT);

        heightUpToHere += landingPosCheckbox.getDisplayedSize().getY();
        setPos(pos);
        setSize(new Vector2D(width, heightUpToHere));
    }

    @Override
    public void render(Vector2D mouse) {
        super.render(mouse);

        if (label != null) {
            FontRenderer.drawString(
                    label,
                    getDisplayedPos().add(0, 2),
                    Color.WHITE,
                    false
            );
        }
        FontRenderer.drawString(
                "Use Landing Position",
                new Vector2D(
                        landingPosCheckbox.getDisplayedPos().getX() + 16,
                        landingPosCheckbox.getDisplayedPos().getY() + 2
                ),
                Color.WHITE,
                false
        );
        FontRenderer.drawString(
                "Count This Tick",
                new Vector2D(
                        countTickCheckbox.getDisplayedPos().getX() + 16,
                        countTickCheckbox.getDisplayedPos().getY() + 2
                ),
                Color.WHITE,
                false
        );
    }

    public void save() {
        bbEditor.save();
        triggerZone.setTriggerMode(triggerModeChoice.getCurrent());
        triggerZone.setPosMode(posModeChoice.getCurrent());
        triggerZone.setUsingLandingPos(landingPosCheckbox.isChecked());
        triggerZone.setCountTriggerTick(countTickCheckbox.isChecked());
    }
}
