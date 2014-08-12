/**
 * 
 */
package org.teapotech.utils.jetty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jiangl
 * 
 */
public class JettyBootstrap {
	private final Logger logger = LoggerFactory.getLogger(JettyBootstrap.class);
	private File APPLICATION_HOME = null;

	private boolean isApplicationHomeValid() {
		String sah = System.getProperty("APPLICATION_HOME");
		if (sah == null)
			return false;
		APPLICATION_HOME = new File(sah);
		return APPLICATION_HOME.exists();
	}

	/**
	 * @param args
	 */
	public void startup() throws Exception {
		if (!isApplicationHomeValid())
			throw new Error("APPLICATION_HOME not valid.");
		System.setProperty("JETTY_HOME", APPLICATION_HOME.getAbsolutePath());

		File confDir = new File(APPLICATION_HOME.getAbsolutePath()
		        + File.separator + "conf");
		List<File> confFiles = getJettyConfigurationFiles();
		if (confFiles.size() == 0)
			throw new FileNotFoundException(
			        "Cannot find jetty configuration files in "
			                + confDir.getAbsolutePath());

		Object[] obj = new Object[confFiles.size()];
		XmlConfiguration last = null;
		for (int i = 0; i < confFiles.size(); i++) {
			File confFile = confFiles.get(i);
			logger.debug("Trying to config with {} ", confFile.getName());
			Resource confRes = Resource.newResource(confFile);
			XmlConfiguration conf = new XmlConfiguration(
			        confRes.getInputStream());
			if (last != null)
				conf.getIdMap().putAll(last.getIdMap());
			obj[i] = conf.configure();
			last = conf;
			logger.info("Configured server with " + confFile.getName());
		}
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] instanceof LifeCycle) {
				LifeCycle lc = (LifeCycle) obj[i];
				if (!lc.isRunning())
					lc.start();
			}
		}
	}

	private List<File> getJettyConfigurationFiles() throws Exception {
		File confDir = new File(APPLICATION_HOME.getAbsolutePath()
		        + File.separator + "conf");
		File jettyConfFile = new File(confDir, "jetty.conf");
		if (!jettyConfFile.exists() || !jettyConfFile.canRead()
		        || jettyConfFile.isDirectory())
			throw new FileNotFoundException("Cannot find "
			        + jettyConfFile.getAbsolutePath());
		LinkedList<File> confFiles = new LinkedList<File>();
		try (BufferedReader br = new BufferedReader(new FileReader(
		        jettyConfFile));) {
			String line = br.readLine();
			while (line != null) {
				line = line.trim();
				if (!line.startsWith("#")) {
					File f = new File(confDir, line);
					if (!f.exists() || !f.canRead())
						throw new FileNotFoundException("Cannot find "
						        + f.getAbsolutePath());
					confFiles.add(f);
				}
				line = br.readLine();
			}
		}
		return confFiles;
	}

	/**
	 * For test only
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new JettyBootstrap().startup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}