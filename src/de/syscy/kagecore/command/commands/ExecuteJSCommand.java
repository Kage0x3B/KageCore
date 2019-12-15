package de.syscy.kagecore.command.commands;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import de.syscy.kagecore.KageCore;
import de.syscy.kagecore.command.CommandBase;
import de.syscy.kagecore.command.argument.StringListArgument;
import de.syscy.kagecore.worldedit.SchematicLoader;
import de.syscy.kagecore.worldedit.SchematicSaver;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

public class ExecuteJSCommand extends CommandBase<KageCore> {
	private ScriptEngine scriptEngine;
	private Map<CommandSender, Bindings> bindingsCache = new WeakHashMap<>();

	public ExecuteJSCommand(KageCore plugin) {
		super(plugin, "exec", StringListArgument.create("script").suggestions(new Function<CommandSender, List<String>>() {
			@Override
			public List<String> apply(CommandSender sender) {
				List<String> suggestions = new ArrayList<>();
				suggestions.addAll(Arrays.asList("sender", "server"));

				if(sender instanceof Player) {
					suggestions.addAll(Arrays.asList("player", "world", "inventory"));
				}

				for(Player player : Bukkit.getOnlinePlayers()) {
					suggestions.add(player.getName());
				}

				return suggestions;
			}
		}).build());

		scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
	}

	@Override
	public void onCommand(CommandSender sender) {
		String script = Joiner.on(' ').join(arguments.getStringList("script"));

		if(script.startsWith("^")) {
			script = "sender.sendMessage(" + script.substring(1, script.length()) + ")";
		}

		Bindings bindings = bindingsCache.get(sender);

		if(bindings == null) {
			bindings = scriptEngine.createBindings();
		}

		bindings.put("sender", sender);
		bindings.put("s", sender);

		if(sender instanceof Player) {
			Player player = (Player) sender;

			bindings.put("player", player);
			bindings.put("p", player);

			bindings.put("world", player.getWorld());
			bindings.put("w", player.getWorld());

			bindings.put("inventory", player.getInventory());
			bindings.put("inv", player.getInventory());
		}

		bindings.put("server", sender.getServer());
		bindings.put("kc", plugin);
		bindings.put("SchematicSaver", SchematicSaver.class);
		bindings.put("SchematicLoader", SchematicLoader.class);

		for(Player player : Bukkit.getOnlinePlayers()) {
			bindings.put(player.getName(), player);
		}

		ScriptContext scriptContext = scriptEngine.getContext();
		scriptContext.setWriter(new ChatWriter(sender, ChatColor.GREEN + "[SCRIPT] "));
		scriptContext.setErrorWriter(new ChatWriter(sender, ChatColor.RED + "[SCRIPT ERROR] "));
		scriptContext.setReader(new Reader() {
			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				return 0;
			}

			@Override
			public void close() throws IOException {

			}
		});
		scriptEngine.setContext(scriptContext);

		try {
			scriptEngine.eval(script, bindings);
			Object msg = scriptEngine.get("msg");

			if(msg != null) {
				sender.sendMessage(ChatColor.GREEN + "[SCRIPT] " + msg.toString());
			}

			sender.sendMessage(ChatColor.GREEN + "Executed script!");
		} catch(Exception ex) {
			sender.sendMessage(ChatColor.RED + "Script Error: " + ex.getMessage());
		}

		bindingsCache.put(sender, bindings);
	}

	@RequiredArgsConstructor
	private static class ChatWriter extends Writer {
		private final CommandSender sender;
		private final String prefix;
		private StringBuilder stringBuilder = new StringBuilder();

		@Override
		public void write(char[] cbuf, int offset, int len) throws IOException {
			stringBuilder.append(cbuf, offset, len);
		}

		@Override
		public void flush() throws IOException {
			for(String string : Splitter.on(System.lineSeparator()).split(stringBuilder.toString())) {
				if(!string.isEmpty()) {
					sender.sendMessage(prefix + ChatColor.RESET + string);
				}
			}
		}

		@Override
		public void close() throws IOException {
			if(stringBuilder.length() > 0) {
				flush();
			}
		}
	}
}