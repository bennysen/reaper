package org.cabbage.commons.utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.cabbage.commons.utils.CharsetUtils;

public class FileUtils {

	public synchronized static List<String> readTxtFile(String filePath) {
		List<String> l = new ArrayList<String>();
		InputStreamReader read = null;
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				read = new InputStreamReader(new FileInputStream(file),
						encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					l.add(lineTxt);
				}
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (IOException e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		} finally {
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return l;
	}

	public synchronized static String readTxtFile2String(String filePath) {
		StringBuffer buffer = new StringBuffer();
		InputStreamReader read = null;
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				read = new InputStreamReader(new FileInputStream(file),
						encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					buffer.append(lineTxt);
				}
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (IOException e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		} finally {
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}

	public synchronized static List<String> readTxtFile(String filePath,
			String encoding) {
		if (null == encoding || CharsetUtils.isInvalidCharset(encoding)) {
			// TODO throw Exception
		}

		List<String> l = new ArrayList<String>();
		InputStreamReader read = null;
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				read = new InputStreamReader(new FileInputStream(file),
						encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					l.add(lineTxt);
				}
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (IOException e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		} finally {
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return l;
	}

	public synchronized static List<String> scanDir(String filePath) {
		List<String> l = new ArrayList<String>();
		File f = new File(filePath);
		if (f.isDirectory()) {
			String dirs[] = f.list();
			for (int i = 0; dirs != null && i < dirs.length; i++) {
				File f2 = new File(filePath + "/" + dirs[i]);
				if (f2.isDirectory()) {
					l.add(f2.getAbsolutePath() + "/");
				}
			}
		}
		return l;
	}

	public synchronized static List<String> scanFile(String filePath) {
		List<String> l = new ArrayList<String>();
		File f = new File(filePath);
		if (f.isDirectory()) {
			String dirs[] = f.list();
			for (int i = 0; dirs != null && i < dirs.length; i++) {
				File f2 = new File(filePath + "/" + dirs[i]);
				if (f2.isFile()) {
					l.add(f2.getAbsolutePath());
				}
			}
		}
		return l;
	}

	/**
	 * 以文件流的方式复制文件
	 * 
	 * @param src
	 *            文件源目录
	 * @param dest
	 *            文件目的目录
	 * @throws FileNotFoundException
	 * @throws IOException
	 * 
	 */
	public synchronized static void copyFile(String src, String dest)
			throws FileNotFoundException, IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(src);
			File file = new File(dest);
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileOutputStream(file);
			int c;
			byte buffer[] = new byte[8192];
			while ((c = in.read(buffer)) != -1) {
				for (int i = 0; i < c; i++)
					out.write(buffer[i]);
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
				in = null;
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					throw e;
				}
				out = null;
			}
		}
	}

	/**
	 * 文件重命名
	 * 
	 * @param path
	 *            文件目录
	 * 
	 * @param oldName
	 *            原来的文件名
	 * 
	 * @param newName
	 *            新文件名
	 * @throws Exception
	 * 
	 */
	public synchronized static void renameFile(String path, String oldName,
			String newName) throws Exception {
		if (!oldName.equals(newName)) {// 新的文件名和以前文件名不同时,才有必要进行重命名
			File oldFile = new File(path + "/" + oldName);
			File newFile = new File(path + "/" + newName);
			if (newFile.exists()) {// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
				System.out.println(newName + "已经存在！");
				throw new Exception("Rename file failed!File name [" + newName
						+ "]is exists!");
			} else {
				oldFile.renameTo(newFile);
			}
		}
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 *            目录
	 * @throws Exception
	 * 
	 */
	public synchronized static void createDir(String path) throws Exception {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		} else {
			System.out.println("Dir is exists：" + path);
			throw new Exception("Create Dir failed!Dir [" + path
					+ "]is exists!");
		}
	}

	/**
	 * 创建新文件
	 * 
	 * @param path
	 *            目录
	 * @param fileName
	 *            文件名
	 * @throws Exception
	 * @throws IOException
	 */
	public synchronized static void createFile(String path, String fileName)
			throws Exception, IOException {
		File file = new File(path + "/" + fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			System.out.println("File is exists：" + path + "/" + fileName);
			throw new Exception("Create file failed!File [" + path + "/"
					+ fileName + "]is exists!");
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 *            目录
	 * @param fileName
	 *            文件名
	 * @throws Exception
	 */
	public synchronized static void deleteFile(String path, String fileName)
			throws Exception {
		File file = new File(path + "/" + fileName);
		if (file.exists() && file.isFile()) {
			file.delete();
		} else {
			throw new Exception("Delete file failed!File [" + path + "/"
					+ fileName + "]is not exists!");
		}

	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 *            文件名
	 * @throws Exception
	 */
	public synchronized static void deleteFile(String fileName)
			throws Exception {
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			file.delete();
		} else {
			throw new Exception("Delete file failed!File [" + fileName
					+ "]is not exists!");
		}

	}

	/**
	 * 获取指定目录下的所有文件信息
	 * 
	 * @param root
	 *            文件目录
	 * @return HashMap<String 文件名（不带文件后缀）, String 文件后缀>
	 */
	// public synchronized static List<String> listFile(String path) {
	// ArrayList<String> fileList = null;
	// File file = new File(path);
	// if (file.isDirectory()) {
	// File[] files = file.listFiles();
	// if (null != files && files.length > 0) {
	// fileList = new ArrayList<String>();
	// for (int i = 0; i < files.length; i++) {
	// if (files[i].isFile()) {
	// File taskFile = files[i];
	// String fileName = taskFile.getName();
	// String fileExtension = fileName.substring(fileName
	// .length() - 3, fileName.length());
	// if (fileExtension.equals("bcp")) {
	// fileList.add(fileName);
	// }
	// }
	// }
	// }
	// }
	// return fileList;
	// }
	public synchronized static List<File> listFiles(File root) {
		ArrayList<File> fileList = null;
		File file = root;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (null != files && files.length > 0) {
				fileList = new ArrayList<File>();
				for (int i = 0; i < files.length; i++) {
					// if (files[i].isFile()) {
					File f = files[i];
					fileList.add(f);
					// }
				}
			}
		}
		return fileList;
	}

	public synchronized static void writeTxtFile(String fileName, String content)
			throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName);
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}

	public synchronized static void writeTxtFile(String fileName,
			String content, String charset) throws IOException {
		OutputStreamWriter osw = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileName, true);
			osw = new OutputStreamWriter(fos, charset);
			osw.write(content);
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != osw) {
				osw.close();
				osw = null;
			}
			if (null != fos) {
				fos.close();
				fos = null;
			}
		}
	}

	public synchronized static void append(String fileName, String content)
			throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName, true);
			writer.write(content);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}

	public synchronized static boolean isExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
