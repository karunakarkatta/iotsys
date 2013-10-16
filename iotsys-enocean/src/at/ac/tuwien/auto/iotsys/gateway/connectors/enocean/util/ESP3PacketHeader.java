/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class ESP3PacketHeader
{
    public enum PacketType
    {
        RADIO(0x01),
        RESPONSE(0x02),
        RADIO_SUB_TEL(0x03),
        EVENT(0x04),
        COMMON_COMMAND(0x05),
        SMART_ACK_COMMAND(0x06),
        REMOTE_MAN_COMMAND(0x07),
        RADIO_MESSAGE(0x09),
        RADIO_ADVANCED(0x0A);

        private byte value;
 
        private PacketType (int value)
        {
            this.value = (byte)(value & 0xFF);
        }
        
        public byte getValue()
        {
        	return this.value;
        }
        
        public static PacketType checkValue(int value)
        {
        	for (PacketType packetType : PacketType.values())
        	{
        		if ( packetType.value == ((byte)(value & 0xFF)))
        		{
        			return packetType;
        		}
        	}
        	return null;
        }
    }

    public static byte ESP3_SYNC = 0x55;
    public static int ESP3_HEADER_SIZE = 0x04;
    private static int HEADER_DATA_LEN_HI_POS = 0;
    private static int HEADER_DATA_LEN_LO_POS = 1;
    private static int HEADER_OPT_DATA_LEN_POS = 2;
    private static int HEADER_PACKET_TYPE_POS = 3;
    
    private int dataLen;
    private int optDataLen;
    private PacketType packetType;

    public ESP3PacketHeader (byte[] data)
    {
    	this.dataLen = (((data[HEADER_DATA_LEN_HI_POS] & 0xFF) << 8) + (data[HEADER_DATA_LEN_LO_POS] & 0xFF));
    	this.optDataLen = (data[HEADER_OPT_DATA_LEN_POS] & 0xFF);
    	this.packetType = PacketType.checkValue((data[HEADER_PACKET_TYPE_POS] & 0xFF));
    }
   
    public int getDataLength()
    {
    	return dataLen;
    }
    
    public int getOptionalDataLength()
    {
    	return optDataLen;
    }
    
    public PacketType getPacketType()
    {
    	return packetType;
    }
    
    public static boolean checkCRC8 (byte[] data, byte crc)
    {
    	byte crc_local;
    	crc_local = CRC8Hash.calculate(data);
    	if (crc_local == crc)
    		return true;
    	
    	return false;
    }
    
    public String toString ()
    {
    	StringBuilder builder = new StringBuilder();
    	
    	String len = String.format ("length=0x%04X, ", dataLen);
    	String optLen = String.format ("optional data length=0x%02X, ", optDataLen);
    	String type = "type=" + packetType;
    	
    	builder
    		.append("[HEADER: ")
    		.append(len)
    		.append(optLen)
    		.append(type)
    		.append("]");
    	return builder.toString();
    }
}
