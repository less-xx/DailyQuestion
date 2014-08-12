/**
 * 
 */
package org.teapotech.dc.app.util;

import java.io.File;
import java.nio.file.Files;

import javax.activation.MimetypesFileTypeMap;

/**
 * @author jiangl
 *
 */
public class ContentTypeDetector {

	private static MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

	public static String detectContentType(File file) {
		String type = null;
		try {
			type = Files.probeContentType(file.toPath());
		} catch (Exception e) {
		}
		if (type == null)
			type = fileTypeMap.getContentType(file);
		return type;
	}
}
