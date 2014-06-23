package com.fox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.Scanner;

/**
 * 
 * 文件转码：utf8->gbk
 * 
 * @author liuxiuquan
 * 
 */
public class FileUtils {

	/**
	 * 转码的对外方法
	 * @param dirPath 需要转码的文件夹
	 * @param decode 解码方式
	 * @param encode 编码方式
	 * @throws IOException
	 */
	public static synchronized void convertDirectory(String dirPath,String decode,String encode)
			throws IOException {
		File dir = new File(dirPath);
		if (!dir.exists() && !dir.isDirectory()) {
			throw new IOException("[" + dir + "] 不存在");
		}
		convert(dir,decode,encode);
	}
	
	/**
	 * 遍历目录 [一级方法]
	 */
	private static void convert(File dir,String decode,String encode) {
		if (dir.canRead() && dir.canWrite()) {
			if (dir.isDirectory()) {
				// Directory
				String[] files = dir.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						// 递归 处理文件
						convert(new File(dir, files[i]),decode,encode);
					}
				}
			} else {
				// 判断是否是java文件
				if (dir.toString().endsWith("java")) {
					System.out.println("convert fileName==" + dir.getName());
					try {
						readFilePath(dir.getPath(), decode, encode);
					} catch (Exception e) {
						System.out.println("Convert failed. File not exsit?");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 解码编码文件
	 * @param filePath 文件路径
	 * @param decode 解码方式 [读]
	 * @param encode 编码方式 [写]
	 * @throws Exception
	 */
	private static void readFilePath(String filePath, String decode,
			String encode) throws Exception {
		// 搞个文件输入流读文件了
		FileInputStream fis = new FileInputStream(filePath);
		InputStreamReader isr = new InputStreamReader(fis, decode);
		BufferedReader br = new BufferedReader(isr);
		File a = new File(filePath);
		// 分离文件夹和文件名
		String fileDir = a.getParent();
		String fileName = a.getName();
		// 新的文件夹命名为原文件名称加上_Convert
		String dir3 = fileDir + "_Convert" + File.separator + fileName;
		File file = new File(dir3);
		if (!file.getParentFile().exists()) {
			// 文件夹不存在则建立文件夹
			file.getParentFile().mkdirs();
		} else {
			// 文件存在则删除文件
			if (file.exists()) {
				file.delete();
			} else {
				// 文件不存在建立文件
				file.createNewFile();
			}
		}
		String str = null;
		while ((str = br.readLine()) != null) {
			//遍历每一行，写入新文件
			saveFile(str, encode, dir3);
		}
		br.close();
	}

	/**
	 * 指定编码写文件
	 * @param fileContent 写入文件的内容
	 * @param encode 编码方式 [写]
	 * @param savePath 新文件的路径
	 * @return
	 */
	private static String saveFile(String fileContent, String encode,
			String savePath) {
		try {
			FileOutputStream fos = new FileOutputStream(savePath, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encode);
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(fileContent);
			bw.newLine();
			bw.close();
			osw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not exist：" + savePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}

	/**
	 * 测试用
	 */
	public static void main(String[] args) throws IOException {
		//记录起始时间
		long startTime = System.currentTimeMillis();
		Properties prop = System.getProperties();
		// 获取桌面位置
		String sysDir = prop.getProperty("user.home") + "\\Desktop\\";
		System.out.println("UTF-8转GBK编码");
		System.out.println("请输入需要转换的文件夹名称：");
		System.out.print(sysDir);
		// 获取控制台输入
		Scanner sca = new Scanner(System.in);
		String dirConvert = sca.next();
		String dirPath = sysDir + dirConvert;
		String decode="UTF-8";
		String encode="GBK";
		convertDirectory(dirPath,decode,encode);
		System.out.println("done!");
		//记录结束时间
		long endTime = System.currentTimeMillis();
		System.out.println("转换完成，用时：" + (endTime - startTime) / 1000);
	}
}