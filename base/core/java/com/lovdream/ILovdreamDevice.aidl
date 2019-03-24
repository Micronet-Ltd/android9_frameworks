
package com.lovdream;

interface ILovdreamDevice{
	void writeToFile(String path,String flag);
	String readToFile(String path);
	void runProcess(String cmds);
	
	/*
	*add by xxf
	*@param int flag; 
	*                            
	*/
	long getMemorySize(int flag);
}
