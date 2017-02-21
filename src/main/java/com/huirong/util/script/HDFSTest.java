package com.huirong.util.script;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSTest {

	public static void main(String[] args)  {
		
		try {
			String mode = args[0];
			
			String f1 = "/data/test/edu/nfcapd.201512241909.txt";
			String f2 = "/data/test/edu/nfcapd.201512241910.txt";
			String f3 = "/data/test/edu/nfcapd.201512241911.txt";
			String path = "/user/yaoxin/edu_last_24/";
			String obs = "hdfs://59.67.152.231:9000/user/yaoxin/edu_last_24/";
			
			if(mode.equals("upload")){
				uploadFile(f1, path);
				uploadFile(f2, path);
				uploadFile(f3, path);
			}
			else if(mode.equals("delete")){
				delete(f1);
			}
			else if(mode.equals("show")){
				showList(obs);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void uploadFile(String src,String dst) throws IOException{
		Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path srcPath = new Path(src); //原路径
        Path dstPath = new Path(dst); //目标路径
        //调用文件系统的文件复制函数,前面参数是指是否删除原文件，true为删除，默认为false
        fs.copyFromLocalFile(false,srcPath, dstPath);
        
        //打印文件路径
        System.out.println("Upload to "+conf.get("fs.default.name"));
        System.out.println("------------list files------------"+"\n");
        FileStatus [] fileStatus = fs.listStatus(dstPath);
        for (FileStatus file : fileStatus) 
        {
            System.out.println(file.getPath());
        }
        fs.close();       
	}
	
	 public static void delete(String filePath) throws IOException{
		 
		 Configuration conf = new Configuration();
         FileSystem fs = FileSystem.get(conf);
         Path path = new Path(filePath);
         boolean isok = fs.deleteOnExit(path);
         if(isok){
             System.out.println("delete ok!");
         }else{
             System.out.println("delete failure");
         }
         fs.close();       
	 }
	 
	 public static void showList(String path){
		 try {
			Configuration con = new Configuration();
				FileSystem fs = FileSystem.get(URI.create(path), con);
				FileStatus [] lls = fs.listStatus(new Path(path));
				int szz = lls.length;
				for(int i = 0;i < szz;i ++){
					String nn = lls[i].getPath().getName();
					System.out.println(nn);
				}
				fs.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

}
