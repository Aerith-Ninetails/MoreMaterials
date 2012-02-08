/*
 The MIT License

 Copyright (c) 2011 Zloteanu Nichita (ZNickq), Sean Porter (Glitchfinder),
 Jan Tojnar (jtojnar, Lisured) and Andre Mohren (IceReaper)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package net.spoutmaterials.spoutmaterials.cmds;

import net.spoutmaterials.spoutmaterials.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminExecutor implements CommandExecutor {

	private Main instance;

	public AdminExecutor(Main plugin) {
		instance = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!instance.hasPermission(sender, "spoutmaterials.debug")) {
			sender.sendMessage(ChatColor.GREEN+"[SpoutMaterials]"+ChatColor.RED+ " You don't have permission to do that!");
			return true;
		}
		
		// Parameter is "load"
		if ("load".equals(args[0]) && args.length == 2) {
			this.instance.smpManager.loadSmp(args[1]);
			this.instance.getLegacyManager().reload();
		}
		
		// Parameter is "unload"
		if ("unload".equals(args[0]) && args.length == 2) {
			this.instance.smpManager.unloadSmp(args[1]);
			this.instance.getLegacyManager().reload();
		}
		
		// Parameter is "reload"
		if ("reload".equals(args[0]) && args.length == 2) {
			this.instance.smpManager.loadSmp(args[1]);
			this.instance.smpManager.unloadSmp(args[1]);
		}
		
		return true;
	}
}