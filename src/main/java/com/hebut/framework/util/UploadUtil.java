package com.hebut.framework.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Fileupload;

import com.hebut.framework.factory.MessageFactory;

public class UploadUtil {

	public static final String UPLOAD_ROOT = "/upload/";

	public static final String DEFAULT_FILENAME = "DEFAULTFILENAME";

	public static final String EXCEL_TYPE = "xls,xlsx";

	public static final String COMPRESS_TYPE = "zip,rar,iso";

	public static final String ALL_TYPE = "ALLTYPE";

	public static String saveFile(String uploadDir, String fileName, String acceptFileType) throws Exception {
		String uploadPath = null;
		Media media = Fileupload.get();
		if (media != null) {
			String fileType = media.getName().substring(media.getName().lastIndexOf('.') + 1);
			if (!acceptFileType.equals(ALL_TYPE) && !validateType(fileType, acceptFileType)) {
				MessageFactory.showMessage("上传文件类型错误！只能为" + acceptFileType);
				return uploadPath;
			}
			uploadDir = Executions.getCurrent().getDesktop().getWebApp().getRealPath(UPLOAD_ROOT) + File.separator + uploadDir;
			File pathDir = new File(uploadDir);
			if (!(pathDir.exists())) {
				if (pathDir.mkdirs() == false) {
					MessageFactory.showMessage("存储路径创建失败！");
					return uploadPath;
				}
			}
			if (fileName.equals(DEFAULT_FILENAME)) {
				uploadPath = uploadDir + File.separator + media.getName();
			} else {
				uploadPath = uploadDir + File.separator + fileName + "." + fileType;
			}
			if (media.isBinary()) {
				InputStream ins = media.getStreamData();
				FileOutputStream fos = new FileOutputStream(uploadPath);
				byte[] buf = new byte[1024];
				int len;
				while ((len = ins.read(buf)) > 0) {
					fos.write(buf, 0, len);
				}
				ins.close();
				fos.close();
			} else if (media.inMemory()) {
				String str = media.getStringData();
				FileOutputStream fos = new FileOutputStream(uploadPath);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
				BufferedWriter bw = new BufferedWriter(osw);
				bw.write(str);
				bw.close();
			}
		}
		return uploadPath;
	}

	public static boolean validateType(String fileType, String acceptFileType) {
		String[] fileTypes = acceptFileType.split(",");
		for (int i = 0; i < fileTypes.length; i++) {
			if (fileTypes[i].equalsIgnoreCase(fileType)) {
				return true;
			}
		}
		return false;
	}

	public static String formatFilePath(String filePath) {
		if (filePath == null) {
			return "";
		} else {
			return filePath.replace(Executions.getCurrent().getDesktop().getWebApp().getRealPath(UPLOAD_ROOT), "~");
		}
	}
}
