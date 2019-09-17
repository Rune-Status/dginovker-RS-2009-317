package org.gielinor.game.system.script.build;

import java.util.Arrays;

import org.gielinor.game.content.dialogue.DialogueInterpreter;
import org.gielinor.game.system.script.ScriptBuilder;
import org.gielinor.game.system.script.ScriptCompiler;
import org.gielinor.game.system.script.ScriptContext;
import org.gielinor.game.system.script.context.OptionDialInstruction;
import org.gielinor.game.system.script.exc.InvalidInstructionException;
import org.gielinor.game.system.script.exc.ScriptException;

/**
 * Handles dialogue script building.
 *
 * @author Emperor
 */
public final class DialogueScriptBuilder extends ScriptBuilder {

    /**
     * Constructs a new {@code DialogueScriptBuilder} {@code Object}.
     *
     * @param args The builder arguments.
     */
    public DialogueScriptBuilder(Object[] args) {
        super(args);
    }

    @Override
    public void configureScript(ScriptContext context) {
        if (arguments[0] instanceof String) {
            for (Object argument : arguments) {
                DialogueInterpreter.add(DialogueInterpreter.getDialogueKey((String) argument), context);
            }
            return;
        }
        for (Object argument : arguments) {
            DialogueInterpreter.add((Integer) argument, context);
        }
    }


    @Override
    public ScriptBuilder create(Object... args) {
        return new DialogueScriptBuilder(args);
    }

    @Override
    public Object[] parseArguments(String args) {
        Object[] objects = null;
        if (args.startsWith("npc:")) {
            args = args.replace("npc:", "").replaceAll(" ", "");
            String[] str = args.split(",");
            objects = new Object[str.length];
            for (int i = 0; i < str.length; i++) {
                objects[i] = Integer.parseInt(str[i]);
            }
        } else if (args.startsWith("type:")) {
            args = args.replace("npc:", "");
            objects = new Object[20];
            int length = 0;
            StringBuilder sb = new StringBuilder();
            boolean creating = false;
            for (int i = 0; i < args.length(); i++) {
                char c = args.charAt(i);
                if (c == '"') {
                    if (creating && sb.length() > 0) {
                        objects[length++] = sb.toString();
                        sb = new StringBuilder();
                    }
                    creating = !creating;
                    continue;
                }
                if (creating) {
                    sb.append(c);
                }
            }
            objects = Arrays.copyOf(objects, length);
        }
        return objects;
    }

    @Override
    public void handleLocalMethod(String name, ScriptContext context, ScriptContext current) throws ScriptException {
        if (name.startsWith("option")) {
            if (!(current instanceof OptionDialInstruction)) {
                throw new InvalidInstructionException("Can't set local method option for instruction " + current + "!", -1);
            }
            OptionDialInstruction ins = (OptionDialInstruction) current;
            StringBuilder sb = new StringBuilder(name.substring("option".length()));
            int index = Integer.parseInt(ScriptCompiler.formatArgument(sb).toString());
            index -= 1;
            if (index < 0 || index >= ins.getOptionHandlers().length) {
                throw new IllegalArgumentException("Index out of bounds - " + index + "!");
            }
            ins.getOptionHandlers()[index] = context;
        }
    }

}