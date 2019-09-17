package plugin.interaction.npc;

import org.gielinor.cache.def.impl.NPCDefinition;
import org.gielinor.game.interaction.OptionHandler;
import org.gielinor.game.node.Node;
import org.gielinor.game.node.entity.player.Player;
import org.gielinor.rs2.plugin.Plugin;

/**
 * Represents the plugin used for gliders.
 *
 * @author 'Vexia
 * @version 1.0
 */
public final class GliderPlugin extends OptionHandler {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        NPCDefinition.forId(3809).getConfigurations().put("option:glider", this);
        NPCDefinition.forId(3810).getConfigurations().put("option:glider", this);
        NPCDefinition.forId(3811).getConfigurations().put("option:glider", this);
        NPCDefinition.forId(3812).getConfigurations().put("option:glider", this);
        NPCDefinition.forId(3813).getConfigurations().put("option:glider", this);
        return this;
    }

    @Override
    public boolean handle(Player player, Node node, String option) {
        player.getDialogueInterpreter().open(3809, node, true);
        return true;
    }

}
