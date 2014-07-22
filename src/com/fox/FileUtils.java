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
 * �ļ�ת�룺utf8->gbk
 * 
 * @author liuxiuquan
 * 
 */
public class FileUtils {

	/**
	 * ת��Ķ��ⷽ��
	 * 
	 * @param dirPath
	 *            ��Ҫת����ļ���
	 * @param decode
	 *            ���뷽ʽ
	 * @param encode
	 *            ���뷽ʽ
	 * @throws IOException
	 */
	public static synchronized void convertDirectory(String dirPath,
			String decode, String encode) throws IOException {
		File dir = new File(dirPath);
		if (!dir.exists() && !dir.isDirectory()) {
			throw new IOException("[" + dir + "] ������");
		}
		convert(dir, decode, encode);
	}

	/**
	 * ����Ŀ¼ [һ������]
	 */
	private static void convert(File dir, String decode, String encode) {
		if (dir.canRead() && dir.canWrite()) {
			if (dir.isDirectory()) {
				// Directory
				String[] files = dir.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						// �ݹ� �����ļ�
						convert(new File(dir, files[i]), decode, encode);
					}
				}
			} else {
				// �ж��Ƿ���java�ļ�
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
	 * ��������ļ�
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @param decode
	 *            ���뷽ʽ [��]
	 * @param encode
	 *            ���뷽ʽ [д]
	 * @throws Exception
	 */
	private static void readFilePath(String filePath, String decode,
			String encode) throws Exception {
		// ����ļ����������ļ���
		FileInputStream fis = new FileInputStream(filePath);
		InputStreamReader isr = new InputStreamReader(fis, decode);
		BufferedReader br = new BufferedReader(isr);
		File originalFile = new File(filePath);

		// �����ļ��к��ļ���
		String fileDir = originalFile.getParent();
		String fileName = originalFile.getName();
		// �µ��ļ�������Ϊԭ�ļ����Ƽ���_Convert
		// String dir3 = fileDir + "_Convert" + File.separator + fileName;
		String dir3 = fileDir + File.separator + fileName + "_Convert";
		File file = new File(dir3);
		if (!file.getParentFile().exists()) {
			// �ļ��в����������ļ���
			file.getParentFile().mkdirs();
		} else {
			// �ļ�������ɾ���ļ�
			if (file.exists()) {
				file.delete();
			}
			// �ļ������ڽ����ļ�
		}
		String str = null;
		while ((str = br.readLine()) != null) {
			// ����ÿһ�У�д�����ļ�
			saveFile(str, encode, dir3);
		}
		br.close();

		// ����Ϊԭ�ļ���
		originalFile.delete();
		file.renameTo(originalFile);
	}

	/**
	 * ָ������д�ļ�
	 * 
	 * @param fileContent
	 *            д���ļ�������
	 * @param encode
	 *            ���뷽ʽ [д]
	 * @param savePath
	 *            ���ļ���·��
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
			System.out.println("File not exist��" + savePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContent;
	}

	/**
	 * ������
	 */
	public static void main(String[] args) throws IOException {

		Properties prop = System.getProperties();
		// ��ȡ����λ��
		String sysDir = prop.getProperty("user.home") + "\\Desktop\\";
		System.out.println("��������Ҫת�����ļ������ƣ�");
		System.out.print(sysDir);
		// ��ȡ����̨����
		Scanner sca = new Scanner(System.in);
		String dirConvert = sca.next();
		String dirPath = sysDir + dirConvert;
		String decode = "GBK"; //��
		String encode = "UTF-8"; //д
		System.out.println(decode+"ת"+encode+"����");
		// ��¼��ʼʱ��
		long startTime = System.currentTimeMillis();
		convertDirectory(dirPath, decode, encode);
		System.out.println("done!");
		// ��¼����ʱ��
		long endTime = System.currentTimeMillis();
		System.out.println("ת����ɣ���ʱ��" + (endTime - startTime) / 1000+"��");
	}
}