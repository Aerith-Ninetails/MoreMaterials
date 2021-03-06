/*
 * This file is part of MoreMaterials.
 * 
 * Copyright (c) 2012 Andre Mohren (IceReaper)
 * 
 * The MIT License
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.morematerials.materials;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.material.Block;
import org.getspout.spoutapi.material.item.GenericCustomTool;

public class MMCustomTool extends GenericCustomTool {

	private String materialName;
	private String smpName;
	private YamlConfiguration config;
	private MoreMaterials plugin;

	public static MMCustomTool create(MoreMaterials plugin, YamlConfiguration yaml, String smpName, String matName) {
		String texture = yaml.getString("Texture");
		
		// Allow to reference textures from other .smp files.
		String[] fileNameParts = texture.split("/");
		if (fileNameParts.length == 2) {
			texture = plugin.getWebManager().getAssetsUrl(fileNameParts[0] + "_" + fileNameParts[1]);
		} else {
			texture = plugin.getWebManager().getAssetsUrl(smpName + "_" + texture);
		}
		
		// TODO use texture Coords (looks like SpoutPlugin needs a patch)
		
		// Build the item.
		return new MMCustomTool(plugin, yaml, texture, smpName, matName);
	}

	public MMCustomTool(MoreMaterials plugin, YamlConfiguration config, String texture, String smpName, String matName) {
		super(plugin, config.getString("Title", matName), texture);
		this.smpName = smpName;
		this.materialName = matName;
		this.config = config;
		this.plugin = plugin;
		
		// Set the items durability
		this.setMaxDurability((short) this.config.getInt("Durability", 0));
		
		// Set the items stackability
		this.setStackable(this.config.getBoolean("Stackable", false));
		
		// Register handlers.
		if (this.config.contains("Handlers")) {
			this.registerHandlers();
		}
	}
	
	private void registerHandlers() {
		Set<String> eventTypes = this.config.getConfigurationSection("Handlers").getKeys(false);
		for (String eventType : eventTypes) {
			if (this.config.contains("Handlers." + eventType)) {
				List<?> handlerList = this.config.getList("Handlers." + eventType);
				for (Object handlerEntry : handlerList) {
					@SuppressWarnings("unchecked")
					Map<String, Object> handlerConfig = (Map<String, Object>) handlerEntry;
					if (this.plugin.getHandlerManager().getHandler((String) handlerConfig.get("Name")) != null) {
						this.plugin.getHandlerManager().registerHandler(eventType, this.getCustomId(), handlerConfig);
					}
				}
			}
		}
	}

	public String getSmpName() {
		return this.smpName;
	}

	public String getMaterialName() {
		return this.materialName;
	}

	public void configureModifiers() {
		if (this.config.contains("ToolLevels")) {
			List<?> blocks = this.config.getList("ToolLevels");
			for (Integer i = 0; i < blocks.size(); i++) {
				String[] blockInfo = ((String) blocks.get(i)).split("[\\s]+");
				Block block = (Block) this.plugin.getSmpManager().getMaterial(this.smpName, blockInfo[0]);
				this.setStrengthModifier(block, Float.parseFloat(blockInfo[1]));
			}
		}
	}

}
