/*
 *  PacketWrapper - Contains wrappers for each packet in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.packetwrapper;

import javax.annotation.Nonnull;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerMap extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MAP;
    
    public WrapperPlayServerMap() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerMap(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve the damage value of the map being modified.
     * @return The current damage value.
    */
    public int getItemDamage() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the damage value of the item being modified.
     * @param value - new value.
    */
    public void setItemDamage(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve length of following byte array.
     * @return The current Text length
    */
    public byte[] getData() {
        return handle.getByteArrays().read(0);
    }
    
    /**
     * Set length of following byte array.
     * @param value - new value.
    */
    public void setData(@Nonnull byte[] value) {
    	if (value == null)
    		throw new IllegalArgumentException("Array cannot be NULL.");
        handle.getByteArrays().write(0, value);
    }
}